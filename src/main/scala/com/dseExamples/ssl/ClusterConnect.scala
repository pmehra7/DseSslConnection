package com.dseExamples.ssl


import java.io.{File, FileInputStream, InputStream}
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import com.datastax.driver.core.{RemoteEndpointAwareJdkSSLOptions, Row, SSLOptions}
import com.datastax.driver.dse.{DseCluster, DseSession}
import com.typesafe.config.ConfigFactory


object ClusterConnect {

    def main(args: Array[String]):Unit = {
      new ClusterConnection()
    }

    // Load Data From Config File
    val config: com.typesafe.config.Config = ConfigFactory.parseFile(new File("config.conf"))

    // Truststore
    var ts: KeyStore = KeyStore.getInstance("JKS")
    var trustStore: InputStream = new FileInputStream(config.getString("trustStorePath"))
    ts.load(trustStore, config.getString("trustStorePassword").toCharArray)
    var tmf: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
    tmf.init(ts)

    // Keystore
    var ks: KeyStore = KeyStore.getInstance("JKS")
    var keyStore: InputStream = new FileInputStream(config.getString("keyStorePath"))
    ks.load(keyStore, config.getString("keyStorePassword").toCharArray)
    var kmf: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(ks, config.getString("keyStorePassword").toCharArray)

    // Initializing the SSLContext
    var ctx: SSLContext = SSLContext.getInstance("TLS")
    ctx.init(kmf.getKeyManagers, tmf.getTrustManagers, new SecureRandom)

    // Create SSL Options
    var cipherSuites: Array[String] = Array("TLS_RSA_WITH_AES_256_CBC_SHA","TLS_DHE_RSA_WITH_AES_256_CBC_SHA","TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA")
    val sslOptions: SSLOptions = RemoteEndpointAwareJdkSSLOptions.builder().withSSLContext(ctx).withCipherSuites(cipherSuites).build()

    class ClusterConnection() {
      // Creating secure cluster connection with SSL and Username + Password
      val clusterIp: String = config.getString("clusterIp")
      val username: String = config.getString("username")
      val password: String = config.getString("password")

      // Simple query to get DSE Release Version
      val clusterBuilder: DseCluster = DseCluster.builder().addContactPoint(clusterIp).withCredentials(username, password).withSSL(sslOptions).build()
      val session: DseSession = clusterBuilder.connect

      // Simple Validation
      System.out.println("Cluster and Session created with SSL")
      val row: Row = session.execute("select release_version from system.local").one()
      println("Release Version: " + row.getString("release_version"))

      // Close cluster and session
      session.close()
      clusterBuilder.close()
      System.out.println("Cluster and Session closed")
    }

}
