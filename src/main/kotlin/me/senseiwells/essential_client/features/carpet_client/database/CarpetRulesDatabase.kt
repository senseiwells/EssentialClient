package me.senseiwells.essential_client.features.carpet_client.database

import carpet.api.settings.CarpetRule
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.senseiwells.essential_client.EssentialClient
import me.senseiwells.essential_client.EssentialClientConfig
import me.senseiwells.essential_client.features.carpet_client.CarpetClient
import me.senseiwells.essential_client.features.carpet_client.yacl.CarpetOptionType
import me.senseiwells.essential_client.utils.CarpetUtils
import me.senseiwells.essential_client.utils.CarpetUtils.matches
import me.senseiwells.essential_client.utils.VersionUtils.getMajorVersion
import net.minecraft.SharedConstants
import net.minecraft.Util
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.CompletableFuture
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

object CarpetRulesDatabase {
    private val cache = EssentialClientConfig.resolve("carpet_rules.json")

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val rules = CompletableFuture.supplyAsync(
        this::getCarpetRulesByMod, Util.backgroundExecutor()
    )

    val registries: Map<String, CarpetRuleDataRegistry>
        get() = rules.join()

    fun getDataForRule(name: String, manager: String): CarpetRuleData? {
        for (rules in registries.values) {
            val data = rules.get(name) ?: continue
            if (data.settingManagers.contains(manager)) {
                return data
            }
        }
        return null
    }

    fun getDataForRule(original: CarpetRule<*>): CarpetRuleData? {
        for (rules in registries.values) {
            val data = rules.get(original.name()) ?: continue
            if (original.matches(data)) {
                return data
            }
        }
        return null
    }

    internal fun load() {

    }

    private fun getCarpetRulesByMod(): Map<String, CarpetRuleDataRegistry> {
        val map = Object2ObjectAVLTreeMap<String, MutableList<CarpetRuleData>>(String::compareTo)
        for (rule in getCarpetRules()) {
            map.getOrPut(rule.modName, ::ObjectArrayList).add(rule)
        }

        val version = SharedConstants.getCurrentVersion()
        val majorVersion = version.getMajorVersion()
        val rulesByMod = Object2ObjectLinkedOpenHashMap<String, CarpetRuleDataRegistry>()
        for ((mod, rules) in map) {
            val builder = CarpetRuleDataRegistry.Builder()
            rules.forEach(builder::add)
            rulesByMod[mod] = builder.build(majorVersion)
        }
        return rulesByMod
    }

    private fun getCarpetRules(): List<CarpetRuleData> {
        var result = downloadCarpetRules()
        if (result.isFailure) {
            val error = result.exceptionOrNull()
            result = readCarpetRules()
            if (result.isFailure) {
                EssentialClient.logger.error("Failed to retrieve carpet rules from database", error)
            }
        } else {
            writeCarpetRules(result.getOrThrow())
        }
        return result.getOrNull() ?: listOf()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun downloadCarpetRules(): Result<List<CarpetRuleData>> {
        return runCatching {
            val url = URI("https://data.carpet.rubixdev.de/data/combined.json").toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            resolveDataWithLocalCarpet(
                cleanupData(json.decodeFromStream<List<CarpetRuleData>>(connection.inputStream))
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeCarpetRules(rules: List<CarpetRuleData>) {
        runCatching {
            cache.outputStream().use { stream ->
                json.encodeToStream(rules, stream)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun readCarpetRules(): Result<List<CarpetRuleData>> {
        return runCatching {
            cache.inputStream().use { stream ->
                resolveDataWithLocalCarpet(json.decodeFromStream<List<CarpetRuleData>>(stream))
            }
        }
    }

    private fun cleanupData(rules: List<CarpetRuleData>): List<CarpetRuleData> {
        return rules.map(::cleanupData)
    }

    private fun cleanupData(data: CarpetRuleData): CarpetRuleData {
        val index = data.options.indexOfFirst { data.defaultValueAsString.equals(it, true) }
        if (index != -1 && data.options[index] != data.defaultValueAsString) {
            val defaultValueAsString = data.options[index]
            return data.copy(
                defaultValueAsString = defaultValueAsString,
                defaultValue = data.type.mapFromString(defaultValueAsString)
            )
        }
        return data
    }

    private fun resolveDataWithLocalCarpet(rules: List<CarpetRuleData>): List<CarpetRuleData> {
        if (!CarpetClient.hasLocalCarpet) {
            return rules
        }
        return rules.map(::resolveDataWithLocalCarpet)
    }

    private fun resolveDataWithLocalCarpet(data: CarpetRuleData): CarpetRuleData {
        val rule = CarpetUtils.rules().find { it.matches(data) }
        if (rule != null) {
            return data.copy(
                type = CarpetOptionType.resolve(rule.type(), rule.categories()),
                defaultValue = rule.defaultValue()
            )
        }
        return data
    }
}