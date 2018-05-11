# Getting Started
- cd runtime
- run ant
- copy parsefilter-qna, parsefilter-inlinks,parse-md and build.xml to $NutchHome/src/plugin
- copy all files from conf folder to $NutchHome/runtime/local/conf
- export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
- enter the seed urls in $NutchHome/runtime/local/urls/seed.txt
- copy paste the command from crawl.sh to console.