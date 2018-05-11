/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.parse.md;

import java.lang.invoke.MethodHandles;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.net.protocols.Response;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.commonmark.node.*;
import org.commonmark.renderer.text.TextContentRenderer;
//import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * ZipParser class based on MSPowerPointParser class by Stephan Strittmatter.
 * Nutch parse plugin for zip files - Content Type : application/zip
 */
public class MdParser implements Parser {

  private static final Logger LOG = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());
  private Configuration conf;

  /** Creates a new instance of ZipParser */
  public MdParser() {
  }

  public ParseResult getParse(final Content content) {

    String resultText = null;
    String resultTitle = null;
    Outlink[] outlinks = null;
    List<Outlink> outLinksList = new ArrayList<Outlink>(); 
    org.commonmark.parser.Parser parser = org.commonmark.parser.Parser.builder().build();
    String doc = new String(content.getContent());
    Node document = parser.parse(doc);
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    TextContentRenderer renderer1 = TextContentRenderer.builder().build();
    //renderer.render(document); 
    resultText=renderer1.render(document); 

    if (resultText == null) {
      resultText = "";
    }

    if (resultTitle == null) {
      resultTitle = "";
    }

    outlinks = (Outlink[]) outLinksList.toArray(new Outlink[0]);
    final ParseData parseData = new ParseData(ParseStatus.STATUS_SUCCESS,
        resultTitle, outlinks, content.getMetadata());

    if (LOG.isTraceEnabled()) {
      LOG.trace("Zip file parsed sucessfully !!");
    }
    return ParseResult.createParseResult(content.getUrl(), new ParseImpl(
        resultText, parseData));
  }

  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  public Configuration getConf() {
    return this.conf;
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.out.println("ZipParser <zip_file>");
      System.exit(1);
    }
    File file = new File(args[0]);
    String url = "file:"+file.getCanonicalPath();
    FileInputStream in = new FileInputStream(file);
    byte[] bytes = new byte[in.available()];
    in.read(bytes);
    in.close();
    Configuration conf = NutchConfiguration.create();
    MdParser parser = new MdParser();
    parser.setConf(conf);
    Metadata meta = new Metadata();
    meta.add(Response.CONTENT_LENGTH, ""+file.length());
    System.out.println("hello");
    ParseResult parseResult = parser.getParse(new Content(url, url, bytes,
        "text/x-web-markdown", meta, conf));
    System.out.println("hi");
    Parse p = parseResult.get(url);
    System.out.println(parseResult.size());
    System.out.println("Parse Text:");
    System.out.println(p.getText());
    System.out.println("Parse Data:");
    System.out.println(p.getData());
  }
}
