package org.w3.validator.nu

import org.w3.validator.{ nu => _, _ }
import nu.validator.servletfilter.{ InboundGzipFilter, InboundSizeLimitFilter }
import nu.validator.servlet.{ MultipartFormDataFilter, VerifierServlet }
//import org.apache.log4j.PropertyConfigurator
//import org.apache.log4j.xml.DOMConfigurator
import org.eclipse.jetty.server.{ Server, Handler }
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.servlet.{ ServletContextHandler, ServletHolder, FilterHolder }
import org.eclipse.jetty.servlets.GzipFilter
import org.eclipse.jetty.util.thread.QueuedThreadPool
import javax.servlet.DispatcherType
import java.util.EnumSet

/**
 * Validator.nu wrapping class
 * @author Hirotaka Nakajima <hiro@w3.org>
 */
class ValidatorNu(prefix: String = "/nu") extends Validator {

  val configuration: ValidatorNuConfiguration = ValidatorNuConfiguration.default
  configuration.setSystemProperties()

  // TODO shouldn't this be either passed?
  val SIZE_LIMIT: Long =
    Integer.parseInt(System.getProperty("nu.validator.servlet.max-file-size", "2097152"))

//  PropertyConfigurator.configure(classOf[ValidatorNu].getClassLoader().getResource("log4j.xml"))

  //  if (!"1".equals(System.getProperty("nu.validator.servlet.read-local-log4j-properties"))) {
  //PropertyConfigurator.configure(classOf[nu.validator.servlet.Main].getClassLoader().getResource("nu/validator/localentities/files/log4j.properties"))
  //  } else {
  //    PropertyConfigurator.configure(System.getProperty("nu.validator.servlet.log4j-properties", "log4j.properties"))
  //  }

  def handler: ServletContextHandler = {
    val context = new ServletContextHandler
    context.setContextPath(prefix)
    val dispatches = EnumSet.of(DispatcherType.REQUEST)
    context.addFilter(new FilterHolder(new GzipFilter), "/*", dispatches)
    context.addFilter(new FilterHolder(new InboundSizeLimitFilter(SIZE_LIMIT)), "/*", dispatches)
    context.addFilter(new FilterHolder(new InboundGzipFilter), "/*", dispatches)
    context.addFilter(new FilterHolder(new MultipartFormDataFilter), "/*", dispatches)
    context.addServlet(new ServletHolder(new VerifierServlet), "/*")
    context
  }

}
