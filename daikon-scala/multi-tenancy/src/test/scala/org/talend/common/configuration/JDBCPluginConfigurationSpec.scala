package org.talend.common.configuration

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import org.talend.common.actor.JDBCPluginConfiguration

@RunWith(classOf[JUnitRunner])
class JDBCPluginConfigurationSpec extends Specification {

  private class DefaultConfig extends Scope {

    val tenantId = "ABCD"

    val tenantConfig: Config = ConfigFactory.parseString(
      """
        |database {
        |      key1 = value1
        |      key2 = value2
        |}
      """.stripMargin)

    val appConfig: Config  = ConfigFactory.parseString(
      """
        |jdbc-snapshot-store {
        |     common-snap-key1 = common-snap-value1
        |     common-snap-key2 = common-snap-value2
        |}
        |
        |jdbc-journal {
        |     common-jnl-key1 = common-jnl-value1
        |     common-jnl-key2 = common-jnl-value2
        |}
        |
      """.stripMargin)
  }

  private class SlickCommonConfig extends DefaultConfig {
    override val appConfig: Config = ConfigFactory.parseString(
      """
        |jdbc-snapshot-store {
        |     common-snap-key1 = common-snap-value1
        |     common-snap-key2 = common-snap-value2
        |     slick {
        |         snap-key3 = snap-value3
        |     }
        |}
        |
        |jdbc-journal {
        |     common-jnl-key1 = common-jnl-value1
        |     common-jnl-key2 = common-jnl-value2
        |     slick {
        |         jnl-key3 = jnl-value3
        |     }
        |}
        |
      """.stripMargin)
  }

  private class OverrideConfig extends DefaultConfig {
    override val appConfig: Config = ConfigFactory.parseString(
      """
        |jdbc-snapshot-store {
        |     common-snap-key1 = common-snap-value1
        |     common-snap-key2 = common-snap-value2
        |     slick {
        |         key1 = common-snap-value1
        |         snap-key3 = snap-value3
        |     }
        |}
        |
        |jdbc-journal {
        |     common-jnl-key1 = common-jnl-value1
        |     common-jnl-key2 = common-jnl-value2
        |     slick {
        |         key1 = common-jnl-value1
        |         jnl-key3 = jnl-value3
        |     }
        |}
        |
      """.stripMargin)
  }


  "The JDBCPluginConfigurationSpec " should {

    "return tenant specific configuration for snapshot store" in new DefaultConfig {

      val jdbcConf = new JDBCPluginConfiguration[String](tenantId, tenantConfig, appConfig)

      val expected = ConfigFactory.parseString(
        s"""
          |jdbc-snapshot-store-$tenantId {
          |    common-snap-key1 = common-snap-value1
          |    common-snap-key2 = common-snap-value2
          |    slick {
          |         key1 = value1
          |         key2 = value2
          |    }
          |}
        """.stripMargin)

      jdbcConf.snapshotPluginConfig shouldEqual expected
    }

    "return tenant specific configuration for journal store" in new DefaultConfig {
      val jdbcConf = new JDBCPluginConfiguration[String](tenantId, tenantConfig, appConfig)

      val expected = ConfigFactory.parseString(
        s"""
           |jdbc-journal-$tenantId {
           |   common-jnl-key1 = common-jnl-value1
           |   common-jnl-key2 = common-jnl-value2
           |   slick {
           |       key1 = value1
           |       key2 = value2
           |   }
           |}
         """.stripMargin)

          jdbcConf.journalPluginConfig shouldEqual expected
    }

    "integrates common slick config for snapshot store" in new SlickCommonConfig {
      val jdbcConf = new JDBCPluginConfiguration[String](tenantId, tenantConfig, appConfig)

      val expected = ConfigFactory.parseString(
        s"""
           |jdbc-snapshot-store-$tenantId {
           |   common-snap-key1 = common-snap-value1
           |   common-snap-key2 = common-snap-value2
           |   slick {
           |       key1 = value1
           |       key2 = value2
           |       snap-key3 = snap-value3
           |   }
           |}
         """.stripMargin)

      jdbcConf.snapshotPluginConfig shouldEqual expected
    }

    "integrates common slick config for journal store" in new SlickCommonConfig {
      val jdbcConf = new JDBCPluginConfiguration[String](tenantId, tenantConfig, appConfig)

      val expected = ConfigFactory.parseString(
        s"""
           |jdbc-journal-$tenantId {
           |   common-jnl-key1 = common-jnl-value1
           |   common-jnl-key2 = common-jnl-value2
           |   slick {
           |       key1 = value1
           |       key2 = value2
           |       jnl-key3 = jnl-value3
           |   }
           |}
         """.stripMargin)

      jdbcConf.journalPluginConfig shouldEqual expected
    }

    "overrides common slick config for snapshot store" in new OverrideConfig {
      val jdbcConf = new JDBCPluginConfiguration[String](tenantId, tenantConfig, appConfig)

      val expected = ConfigFactory.parseString(
        s"""
           |jdbc-snapshot-store-$tenantId {
           |   common-snap-key1 = common-snap-value1
           |   common-snap-key2 = common-snap-value2
           |   slick {
           |       key1 = value1
           |       key2 = value2
           |       snap-key3 = snap-value3
           |   }
           |}
         """.stripMargin)

      jdbcConf.snapshotPluginConfig shouldEqual expected
    }

    "overrides common slick config for journal store" in new OverrideConfig {
      val jdbcConf = new JDBCPluginConfiguration[String](tenantId, tenantConfig, appConfig)

      val expected = ConfigFactory.parseString(
        s"""
           |jdbc-journal-$tenantId {
           |   common-jnl-key1 = common-jnl-value1
           |   common-jnl-key2 = common-jnl-value2
           |   slick {
           |       key1 = value1
           |       key2 = value2
           |       jnl-key3 = jnl-value3
           |   }
           |}
         """.stripMargin)

      jdbcConf.journalPluginConfig shouldEqual expected
    }

  }

}
