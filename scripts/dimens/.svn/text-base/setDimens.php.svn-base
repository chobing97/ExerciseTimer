#!/usr/bin/php
<?php
function main($argc, $argv) {
	if ($argc < 4) {
		usage();
		return 1;
	}

	$normal      = $argv[1];
	$targetWidth = $argv[2];
	$targetPath  = $argv[3];

	$fp = fopen($normal, "r");
	if (!$fp) {
		echo "ERROR : file open failed. - $normal";
		return 1;
	}

	$xml="";
	if($fp) {
		while(($line = fgets($fp)) !== false) {
			$xml .= $line;
		}
	}
	fclose($fp);

	$newXML = getNewXMLString($xml, $targetWidth/320);

	$fp = fopen($targetPath, "w");
	fwrite($fp, $newXML);
	fclose($fp);
}

function getNewXMLString($xml, $scale) {
	$newDom = new DOMDocument();
	$rscElement = $newDom->createElement('resources');
	$newDom->appendChild($rscElement);

	try {
		$xmlDoc = new DOMDocument();
		$xmlDoc->loadXML($xml);

		$nodes = $xmlDoc->getElementsByTagName('dimen');
		for($i=0; $i < $nodes->length; $i++) {
			$node = $nodes->item($i);
			$dp = intval($node->nodeValue);

			$dimen = $newDom->createElement('dimen');
			$dimen->setAttribute('name',$node->getAttribute('name'));
			$newValue = round($dp*$scale);

			if (strpos($node->nodeValue, 'dp') !== false)
				$dimen->nodeValue = $newValue."dp";
			else if (strpos($node->nodeValue, 'sp') !== false)
				$dimen->nodeValue = $newValue."sp";
			else if (strpos($node->nodeValue, 'px') !== false)
				$dimen->nodeValue = $newValue."px";

			$rscElement->appendChild($dimen);
		}

	} catch (Exception $e) {
		error_log('Caught exception: '.$e->getMessage());
		return 1;
	}

	$newXML = str_replace("><", ">\n<", $newDom->saveXML(null));
	return $newXML;
}

function getXMLDataByTagName($xmlDoc, $tagName) {
	try {
		$nodes = $xmlDoc->getElementsByTagName($tagName)->item(0);
		$data = $nodes->firstChild->wholeText;

	} catch (Exception $e) {
		throw $e;
	}

	return $data;
}

function usage() {
	global $argv;

	echo "Usage : $argv[0] NORMAL_DIMENS_FILEPATH(320x) TARGET_DP_WIDTH TARGET_PATH\n";
}

exit(main($argc,$argv));
?>
