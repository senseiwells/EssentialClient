package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;

import java.util.concurrent.CompletableFuture;

public record WaitingEvent(Interpreter interpreter, CompletableFuture<ClassInstance> future) { }
