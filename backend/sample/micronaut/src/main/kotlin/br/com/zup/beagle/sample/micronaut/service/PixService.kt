package br.com.zup.beagle.sample.micronaut.service

import br.com.zup.beagle.sample.pix.PixScreenBuilder
import javax.inject.Singleton

@Singleton
class PixService {
    fun createPixScreen()  = PixScreenBuilder
}
