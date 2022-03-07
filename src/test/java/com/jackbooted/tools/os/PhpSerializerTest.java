package com.jackbooted.tools.os;
import junit.framework.TestCase;


public class PhpSerializerTest extends TestCase {

	public void testParseNull() throws Exception {
		String input = "N;";
		PHP.Element result = PHP.unserialize ( input );
		assertFalse ( result.isset () );
	}

	public void testParseInteger() throws Exception {
		assertPrimitive("i:123;", 123);
	}

	public void testParseFloat() throws Exception {
		assertPrimitive("d:123.123;", 123.123d);
	}

	public void testParseBoolean() throws Exception {
		assertPrimitive("b:1;", Boolean.TRUE);
	}

	public void testParseString() throws Exception {
		assertPrimitive("s:6:\"string\";", "string");
	}

    public void testParseArray() throws Exception {
		String input = "a:1:{i:1;i:2;}";
		PHP.Element result = PHP.unserialize ( input );
		assertEquals(1, result.size());
		assertEquals(2, result.get("1").v());
	}

    public void testParseComplexDataStructure() throws Exception {
		String input;

		// sample output of a yahoo web image search api call
		input = "a:1:{s:9:\"ResultSet\";a:4:{s:21:\"totalResultsAvailable\";s:7:\"1177824\";s:20:\"totalResultsReturned\";" +
				"i:2;s:19:\"firstResultPosition\";i:1;s:6:\"Result\";a:2:{i:0;a:10:{s:5:\"Title\";s:12:\"corvette.jpg\";" +
				"s:7:\"Summary\";s:150:\"bluefirebar.gif 03-Nov-2003 19:02 22k burning_frax.jpg 05-Jul-2002 14:34 169k corvette.jpg " +
				"21-Jan-2004 01:13 101k coupleblack.gif 03-Nov-2003 19:00 3k\";s:3:\"Url\";" +
				"s:48:\"http://www.vu.union.edu/~jaquezk/MG/corvette.jpg\";s:8:\"ClickUrl\";" +
				"s:48:\"http://www.vu.union.edu/~jaquezk/MG/corvette.jpg\";s:10:\"RefererUrl\";" +
				"s:35:\"http://www.vu.union.edu/~jaquezk/MG\";s:8:\"FileSize\";" +
				"s:7:\"101.5kB\";s:10:\"FileFormat\";s:4:\"jpeg\";s:6:\"Height\";s:3:\"768\";" +
				"s:5:\"Width\";s:4:\"1024\";s:9:\"Thumbnail\";a:3:{s:3:\"Url\";s:42:\"http://sp1.mm-a1.yimg.com/image/2178288556\";" +
				"s:6:\"Height\";s:3:\"120\";s:5:\"Width\";s:3:\"160\";}}i:1;a:10:{s:5:\"Title\";" +
				"s:23:\"corvette_c6_mini_me.jpg\";s:7:\"Summary\";s:48:\"Corvette I , Corvette II , Diablo , Enzo , Lotus\";" +
				"s:3:\"Url\";s:54:\"http://www.ku4you.com/minicars/corvette_c6_mini_me.jpg\";s:8:\"ClickUrl\";" +
				"s:54:\"http://www.ku4you.com/minicars/corvette_c6_mini_me.jpg\";s:10:\"RefererUrl\";" +
				"s:61:\"http://mik-blog.blogspot.com/2005_03_01_mik-blog_archive.html\";s:8:\"FileSize\";s:4:\"55kB\";" +
				"s:10:\"FileFormat\";s:4:\"jpeg\";s:6:\"Height\";s:3:\"518\";s:5:\"Width\";s:3:\"700\";" +
				"s:9:\"Thumbnail\";a:3:{s:3:\"Url\";s:42:\"http://sp1.mm-a2.yimg.com/image/2295545420\";" +
				"s:6:\"Height\";s:3:\"111\";s:5:\"Width\";s:3:\"150\";}}}}}";
		PHP.Element results = PHP.unserialize ( input );
		assertEquals(2, results.get("ResultSet").get("Result").size());
	}

	private void assertPrimitive(String input, Object expected) {
		assertEquals(expected, PHP.unserialize ( input ).v() );
	}
	
    public void testAcceptedAttributeNames() throws Exception {
		// sample output of a yahoo web image search api call
		String input = "a:1:{s:9:\"ResultSet\";a:4:{s:21:\"totalResultsAvailable\";s:7:\"1177824\";s:20:\"totalResultsReturned\";" +
			"i:2;s:19:\"firstResultPosition\";i:1;s:6:\"Result\";a:2:{i:0;a:10:{s:5:\"Title\";s:12:\"corvette.jpg\";" +
			"s:7:\"Summary\";s:150:\"bluefirebar.gif 03-Nov-2003 19:02 22k burning_frax.jpg 05-Jul-2002 14:34 169k corvette.jpg " +
			"21-Jan-2004 01:13 101k coupleblack.gif 03-Nov-2003 19:00 3k\";s:3:\"Url\";" +
			"s:48:\"http://www.vu.union.edu/~jaquezk/MG/corvette.jpg\";s:8:\"ClickUrl\";" +
			"s:48:\"http://www.vu.union.edu/~jaquezk/MG/corvette.jpg\";s:10:\"RefererUrl\";" +
			"s:35:\"http://www.vu.union.edu/~jaquezk/MG\";s:8:\"FileSize\";" +
			"s:7:\"101.5kB\";s:10:\"FileFormat\";s:4:\"jpeg\";s:6:\"Height\";s:3:\"768\";" +
			"s:5:\"Width\";s:4:\"1024\";s:9:\"Thumbnail\";a:3:{s:3:\"Url\";s:42:\"http://sp1.mm-a1.yimg.com/image/2178288556\";" +
			"s:6:\"Height\";s:3:\"120\";s:5:\"Width\";s:3:\"160\";}}i:1;a:10:{s:5:\"Title\";" +
			"s:23:\"corvette_c6_mini_me.jpg\";s:7:\"Summary\";s:48:\"Corvette I , Corvette II , Diablo , Enzo , Lotus\";" +
			"s:3:\"Url\";s:54:\"http://www.ku4you.com/minicars/corvette_c6_mini_me.jpg\";s:8:\"ClickUrl\";" +
			"s:54:\"http://www.ku4you.com/minicars/corvette_c6_mini_me.jpg\";s:10:\"RefererUrl\";" +
			"s:61:\"http://mik-blog.blogspot.com/2005_03_01_mik-blog_archive.html\";s:8:\"FileSize\";s:4:\"55kB\";" +
			"s:10:\"FileFormat\";s:4:\"jpeg\";s:6:\"Height\";s:3:\"518\";s:5:\"Width\";s:3:\"700\";" +
			"s:9:\"Thumbnail\";a:3:{s:3:\"Url\";s:42:\"http://sp1.mm-a2.yimg.com/image/2295545420\";" +
			"s:6:\"Height\";s:3:\"111\";s:5:\"Width\";s:3:\"150\";}}}}}";

		PHP.Element result = PHP.unserialize ( input );
		assertEquals(2, result.get("ResultSet").get("totalResultsReturned").v());
	}

}
