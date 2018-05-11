bin/nutch inject crawl/crawldb urls
bin/nutch generate crawl/crawldb crawl/segments
s1=`ls -d crawl/segments/2* | tail -1`
bin/nutch fetch $s1
bin/nutch parse $s1
bin/nutch index crawl/crawldb/ -linkdb crawl/linkdb/ $s1