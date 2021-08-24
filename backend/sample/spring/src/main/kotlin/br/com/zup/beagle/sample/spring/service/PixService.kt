package br.com.zup.beagle.sample.spring.service

import br.com.zup.beagle.sample.pix.PixScreenBuilder
import org.springframework.stereotype.Service

@Service
class PixService {
    fun createPixScreen() = PixScreenBuilder
}