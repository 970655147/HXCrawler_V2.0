/**
 * file name : Main.java
 * created at : 8:40:10 PM Jul 24, 2015
 * created by 970655147
 */

package com.hx.test;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hx.crawler.util.CrawlerUtils;
import com.hx.log.util.Log;

import com.hx.json.JSONArray;

// Main 
public class Test01TestXpathParser {

	// ≤‚ ‘crawlerTools
	// 1. com.hx.test for indexString
	// 2. dom4j ≤‚ ‘
	// 3. com.hx.test for Parser.parse
	public static void main(String []args) throws Exception {
		
		// ------------------1---------------------
////		String str = "[{\"name\":\"product\",\"xpath\":\"/html/body/product\",\"values\":[{\"name\":\"fullName\",\"attribute\":\"text\"}]}]";
//		String str = "[{\"name\":\"product\",\"xpath\":\"/html/body/product\",\"values\":[{\"name\":\"fullName\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./p/price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruit\",\"values\":[{\"name\":\"fullName\",\"xpath\":\"./div/name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"//p/price\",\"attribute\":\"text\"}]}]";
//		IndexString idxStr = new IndexString(str);
//
//		Log.log(idxStr.toString() );
////		Log.log(idxStr.getRoot().getChild(1).toString() );
		
		
		// ------------------2---------------------
//		String test01Product = System.getProperty("user.dir") + "/com.hx.test/com/hx/crawler/com.hx.test/config/Test01Product.html";
//		
//		SAXReader saxReader = new SAXReader();
//		Document document = saxReader.read(test01Product);
//		List<Element> eles = document.selectNodes("/html/body/products/product");
//		Log.log(eles.size() );
		
		
		// ------------------3---------------------
		String test01Product = System.getProperty("user.dir") + "/src/test/java/com/hx/test/config/Test01Product.html";
		// text
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]}]";
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]}]";
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]}]";
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"names\",\"xpath\":\"//name\",\"attribute\":\"text\"}]";
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"names\",\"xpath\":\"//name\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"attribute\":\"text\"}]}]";
		
		// innertext
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"names\",\"xpath\":\"//name\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"attribute\":\"innertext\"}]}]";
		
		// innerhtml
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"names\",\"xpath\":\"//name\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"attribute\":\"innerhtml\"}]}]";
	
		// outerhtml
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"names\",\"xpath\":\"//name\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"attribute\":\"outerhtml\"}]}]";
		
		// other attribute
//		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"fruit\",\"xpath\":\"/html/body/fruits/fruit\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"name\",\"xpath\":\"./name\",\"attribute\":\"text\"},{\"name\":\"price\",\"xpath\":\"./price\",\"attribute\":\"text\"}]},{\"name\":\"names\",\"xpath\":\"//name\",\"values\":[{\"name\":\"index\",\"attribute\":\":index\"},{\"name\":\"url\",\"attribute\":\"href\"}]}]";
		String xpath = "[{\"name\":\"product\",\"xpath\":\"/html/body/products/product\",\"values\":[{\"xpath\":\"./price\",\"attribute\":\"innertext\"}]}]";
		
		SAXReader saxReader = new SAXReader();
		Element root = saxReader.read(test01Product).getRootElement();
		JSONArray fetchedData = CrawlerUtils.xpathParser.parse(root, "http://www.baidu.com/", xpath);
		Log.log(fetchedData.toString() );
		
	}
	
}
