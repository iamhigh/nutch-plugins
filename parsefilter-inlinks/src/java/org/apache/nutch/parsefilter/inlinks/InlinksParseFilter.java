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
package org.apache.nutch.parsefilter.inlinks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.StringUtils;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.parse.*;
import org.apache.nutch.util.*;
//import java.net.URL;
//import java.net.MalformedURLException;

import java.lang.invoke.MethodHandles;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class InlinksParseFilter implements HtmlParseFilter {
  private Configuration conf;

  private static final Logger LOG = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());
      public void setConf(Configuration conf) {
    

  }

  public Configuration getConf() {
    return this.conf;
  }

  @Override
  public ParseResult filter(Content content, ParseResult parseResult,
      HTMLMetaTags metaTags, DocumentFragment doc) {

    //System.out.println(content.getBaseUrl());
      Parse pimp = parseResult.get(content.getBaseUrl());
      ParseData pdata = pimp.getData();
      ParseResult parseResult1=new ParseResult(content.getBaseUrl());
      String doc2 = new String(content.getContent());
      Document docs = Jsoup.parse(doc2);
      Elements links = docs.select("a[href^=#]");
      int count = 0;
      Element previous=null;
      for(Element element : links){
          if(count>0){
          String href = previous.attr("href") ;
          href = href.replace("#", "");
          Element current = docs.getElementById(href);
          String href1 = element.attr("href");
          href1 = href1.replace("#","");
          if(href1.isEmpty())
            continue;
          Element next = docs.getElementById(href1);
          if(next==null)
            continue;
          if(current==null)
            current=next;
          System.out.println(href1);
          System.out.println(next);
          System.out.println(href);
          System.out.println(current);
          Elements current1=current.parents();
          Elements next1 = next.parents();
          Stack<Element> st = new Stack();
          Stack<Element> st1 = new Stack();
          for(Element pars : current1)
            st.push(pars);
          for(Element pars : next1)
            st1.push(pars);
          Element lca=null;
          while(!st.empty()&&!st1.empty()){
                Element x1=st.pop();
                Element x2=st1.pop();
                if(x1!=x2)
                  break;
                lca=x1;
          }
          Elements childs = lca.children​();
          String text="\0";
            for(Element child : childs){
              if(child.getElementById(href)!=null){
              while(child.getElementById(href1)==null){
                text=text+child.text();
                child=child.nextElementSibling​();
                if(child==null)
                  break;
              }
              break; 
            }
            }
            ParseText parseText = new ParseText(text);
          //ParseText parseText = new ParseText(docs.getElementById(href).text());
          //Outlink[] outlinks = OutlinkExtractor.getOutlinks(docs.getElementById(href).text())
          String base = content.getBaseUrl()+"#"+href;
          System.out.println(base);
          ParseData parseData = new ParseData(pdata.getStatus(),content.getBaseUrl(),pdata.getOutlinks(),pdata.getContentMeta());
          if(parseText.getText()!=null)
          parseResult.put(base,parseText,parseData);
      }
      previous=element;
      count=count+1;
      }
    return parseResult;
  }

}
