package org.woowa.weathercodi.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cloud.aws")
class AwsProperties {
    lateinit var credentials: Credentials
    lateinit var region: Region
    lateinit var s3: S3

    class Credentials {
        lateinit var accessKey: String
        lateinit var secretKey: String
    }

    class Region {
        lateinit var static: String
    }

    class S3 {
        lateinit var bucket: String
    }
}