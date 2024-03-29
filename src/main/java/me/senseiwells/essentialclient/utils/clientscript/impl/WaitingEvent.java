package me.senseiwells.essentialclient.utils.clientscript.impl;

import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.interpreter.Interpreter;

import java.util.concurrent.CompletableFuture;

public record WaitingEvent(Interpreter interpreter, CompletableFuture<ClassInstance> future) { }
