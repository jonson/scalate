/**
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.scalate.servlet

;

import javax.servlet.ServletConfig
import org.fusesource.scalate.{Binding, TemplateEngine}
import org.fusesource.scalate.util.ClassPathBuilder
import java.io.File
import scala.tools.nsc.Global;

/**
 * A TemplateEngine which initializes itself using a ServletConfig
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
class ServletTemplateEngine(var config: ServletConfig) extends TemplateEngine {
  bindings = List(Binding("context", classOf[ServletRenderContext].getName, true))
  workingDirectory = new File(config.getServletContext.getRealPath("WEB-INF/_scalate/"))
  workingDirectory.mkdirs
  classpath = buildClassPath
  resourceLoader = new ServletResourceLoader(config.getServletContext)

  private def buildClassPath(): String = {

    val builder = new ClassPathBuilder

    // Add optional classpath prefix via web.xml parameter
    builder.addEntry(config.getInitParameter("compiler.classpath.prefix"))

    // Add containers class path
    builder.addPathFrom(getClass)
            .addPathFrom(classOf[ServletConfig])
            .addPathFrom(classOf[Product])
            .addPathFrom(classOf[Global])

    // Always include WEB-INF/classes and all the JARs in WEB-INF/lib just in case
    builder.addClassesDir(config.getServletContext.getRealPath("/WEB-INF/classes"))
            .addLibDir(config.getServletContext.getRealPath("/WEB-INF/lib"))

    // Add optional classpath suffix via web.xml parameter
    builder.addEntry(config.getInitParameter("compiler.classpath.suffix"))

    builder.classPath
  }
}
