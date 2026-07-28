package com.oficina_execution_service.infra.controller

import com.oficina_execution_service.application.ExecutionService
import com.oficina_execution_service.infra.dto.ExecutionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/execucoes")
@Tag(name = "Execuções")
class ExecutionController(
    private val service: ExecutionService
) {
    @GetMapping("/os/{osId}")
    @Operation(summary = "Consulta a execução de uma OS")
    fun findByOsId(@PathVariable osId: Long): ExecutionResponse =
        ExecutionResponse.from(service.findByOsId(osId))

    @GetMapping("/fila")
    @Operation(summary = "Lista a fila por ordem de entrada")
    fun queue(): List<ExecutionResponse> =
        service.findQueue().map(ExecutionResponse::from)

    @PatchMapping("/os/{osId}/iniciar")
    @Operation(summary = "Inicia o reparo da próxima OS aprovada")
    fun start(@PathVariable osId: Long): ExecutionResponse =
        ExecutionResponse.from(service.start(osId))

    @PatchMapping("/os/{osId}/finalizar")
    @Operation(summary = "Finaliza a execução e comunica o OS Service")
    fun complete(@PathVariable osId: Long): ExecutionResponse =
        ExecutionResponse.from(service.complete(osId))
}
