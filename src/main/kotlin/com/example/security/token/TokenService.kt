package com.example.security.token

interface TokenService {

    fun generate(
        config: TokenConfig,
        vararg tokenClaim: TokenClaim
    ): String

}