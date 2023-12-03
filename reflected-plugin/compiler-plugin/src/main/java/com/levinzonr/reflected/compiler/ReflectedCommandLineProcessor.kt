package com.levinzonr.reflected.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class ReflectedCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "reflected"
    override val pluginOptions: Collection<CliOption> = listOf()
}
