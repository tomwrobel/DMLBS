package org.exist.indexing.lucene;

import org.apache.lucene.analysis.*;

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

/**
 * A filter that replaces accented characters in the ISO Latin 1 character set 
 * (ISO-8859-1) by their unaccented equivalent. The case will not be altered.
 * <p>
 * For instance, '&agrave;' will be replaced by 'a'.
 * <p>
 */

/** Based on ISOLatin1AccentFilter by Mike Sokolov, generous thanks go
 *  to him. Edited by Tom Wrobel (thomas.wrobel@classics.ox.ac.uk) for 
 *  use on the Dictionary of Medieval Latin from British Sources
**/

public class DMLBSAccentFilter extends TokenFilter {
  public DMLBSAccentFilter(TokenStream input) {
    super(input);
  }

  private char[] output = new char[256];
  private int outputPos;

  public final Token next(final Token reusableToken) throws java.io.IOException {
    assert reusableToken != null;
    Token nextToken = input.next(reusableToken);
    if (nextToken != null) {
      final char[] buffer = nextToken.termBuffer();
      final int length = nextToken.termLength();
      // If no characters actually require rewriting then we
      // just return token as-is:
      for(int i=0;i<length;i++) {
        final char c = buffer[i];
        if (c >= '\u00c0' && c <= '\uFB06') {
          removeAccents(buffer, length);
          nextToken.setTermBuffer(output, 0, outputPos);
          break;
        }
      }
      return nextToken;
    } else
      return null;
  }

  /**
   * To replace accented characters in a String by unaccented equivalents.
   */
  public final void removeAccents(char[] input, int length) {

    // Worst-case length required:
    final int maxSizeNeeded = 2*length;

    int size = output.length;
    while (size < maxSizeNeeded)
      size *= 2;

    if (size != output.length)
      output = new char[size];

    outputPos = 0;

    int pos = 0;

    for (int i=0; i<length; i++, pos++) {
      final char c = input[pos];

      // Quick test: if it's not in range then just keep
      // current character
      if (c < '\u00c0' || c > '\uFB06')
        output[outputPos++] = c;
      else {
        switch (c) {
        case '\u00C0' : // À
        case '\u00C1' : // Á
        case '\u00C2' : // Â
        case '\u00C3' : // Ã
        case '\u00C4' : // Ä
        case '\u00C5' : // Å
        case '\u0100' : // A macron
          output[outputPos++] = 'A';
          break;
          
        case '\u00C6' : // Æ
          output[outputPos++] = 'A';
          output[outputPos++] = 'e';
          break;
          
        case '\u00C7' : // Ç
        case '\u010C' : // C caron
          output[outputPos++] = 'C';
          break;
          
        case '\u00C8' : // È
        case '\u00C9' : // É
        case '\u00CA' : // Ê
        case '\u00CB' : // Ë
          output[outputPos++] = 'E';
          break;
          
        case '\u1E24' : // Ḥ — LATIN CAPITAL LETTER H WITH DOT BELOW
          output[outputPos++] = 'H';
          break;       
        
        case '\u00CC' : // Ì
        case '\u00CD' : // Í
        case '\u00CE' : // Î
        case '\u00CF' : // Ï
        case '\u012A' : // Ī
        case '\u012C' : // Ĭ
        case '\u0208' : // Ȉ — LATIN CAPITAL LETTER I WITH DOUBLE GRAVE
          output[outputPos++] = 'I';
          break;
          
        case '\u0132' : // Ĳ
            output[outputPos++] = 'I';
            output[outputPos++] = 'J';
            break;
            
        case '\u00D0' : // Ð
          output[outputPos++] = 'D';
          break;
          
        case '\u00D1' : // Ñ
          output[outputPos++] = 'N';
          break;
          
        case '\u00D2' : // Ò
        case '\u00D3' : // Ó
        case '\u00D4' : // Ô
        case '\u00D5' : // Õ
        case '\u00D6' : // Ö
        case '\u00D8' : // Ø
          output[outputPos++] = 'O';
          break;
          
        case '\u0152' : // Œ
          output[outputPos++] = 'O';
          output[outputPos++] = 'e';
          break;
          
        //thorn, eth, wynn and yogh left as is.
        //case '\u00DE' : // Þ
        //  output[outputPos++] = 'T';
        //  output[outputPos++] = 'H';
        //  break;
        
        case '\u00D9' : // Ù
        case '\u00DA' : // Ú
        case '\u00DB' : // Û
        case '\u00DC' : // Ü
        case '\u016A' : // U macron
          output[outputPos++] = 'U';
          break;
          
        case '\u00DD' : // Ý
        case '\u0178' : // Ÿ
          output[outputPos++] = 'Y';
          break;
          
        case '\u0179': // Z'
        case '\u017D': // Z caron
          output[outputPos++] = 'Z';
          break;
          
        case '\u00DF' : // ß — LATIN SMALL LETTER SHARP S
          output[outputPos++] = 's';
          output[outputPos++] = 's';
          break;
          
        case '\u00E0' : // à
        case '\u00E1' : // á
        case '\u00E2' : // â
        case '\u00E3' : // ã
        case '\u00E4' : // ä
        case '\u00E5' : // å
        case '\u0101' : // ā macron
        case '\u0103' : // ă — LATIN SMALL LETTER A WITH BREVE
        case '\u01CE' : // ǎ — LATIN SMALL LETTER A WITH CARON
        case '\u0203' : // ȃ — LATIN SMALL LETTER A WITH INVERTED BREVE
        case '\u1EA1' : // ạ — LATIN SMALL LETTER A WITH DOT BELOW
          output[outputPos++] = 'a';
          break;
        case '\u00E6' : // æ
        case '\u01E3' : // ǣ — LATIN SMALL LETTER AE WITH MACRON
        case '\u01FD' : // ǽ — LATIN SMALL LETTER AE WITH ACUTE
          output[outputPos++] = 'a';
          output[outputPos++] = 'e';
          break;
          
        case '\u00E7' : // ç
        case '\u010D' : // č caron
          output[outputPos++] = 'c';
          break;
          
        case '\u010F' : // ď — LATIN SMALL LETTER D WITH CARON
		case '\u0111' : // đ — LATIN SMALL LETTER D WITH STROKE
		case '\u1E0D' : // ḍ — LATIN SMALL LETTER D WITH DOT BELOW
          output[outputPos++] = 'd';
          break;	
          	
        case '\u00E8' : // è
        case '\u00E9' : // é
        case '\u00EA' : // ê
        case '\u00EB' : // ë
        case '\u0113' : // ē — LATIN SMALL LETTER E WITH MACRON
		case '\u0115' : // ĕ — LATIN SMALL LETTER E WITH BREVE
		case '\u0119' : // ę — LATIN SMALL LETTER E WITH OGONEK
		case '\u011B' : // ě — LATIN SMALL LETTER E WITH CARON
          output[outputPos++] = 'e';
          break;
          
        case '\u011D' : // ĝ — LATIN SMALL LETTER G WITH CIRCUMFLEX
          output[outputPos++] = 'g';
          break;    
             
		case '\u1E25' : // ḥ — LATIN SMALL LETTER H WITH DOT BELOW
          output[outputPos++] = 'h';
          break;            
             
        case '\u00EC' : // ì
        case '\u00ED' : // í
        case '\u00EE' : // î
        case '\u00EF' : // ï
        case '\u0129' : // ĩ — LATIN SMALL LETTER I WITH TILDE
        case '\u012B' : // ī — LATIN SMALL LETTER I WITH MACRON
        case '\u012D' : // ĭ — LATIN SMALL LETTER I WITH BREVE
        case '\u01D0' : // ǐ — LATIN SMALL LETTER I WITH CARON
          output[outputPos++] = 'i';
          break;
          
        case '\u0133' : // ĳ
            output[outputPos++] = 'i';
            output[outputPos++] = 'j';
            break;
            
        case '\u0237' : // ȷ — LATIN SMALL LETTER DOTLESS J
            output[outputPos++] = 'j';
            break;
            
		case '\u1E35' : // ḵ — LATIN SMALL LETTER K WITH LINE BELOW
			output[outputPos++] = 'k';
            break;
                      
        //thorn, eth, wynn and yogh left as is.
        //case '\u00F0' : // ð
        //  output[outputPos++] = 'd';
        //  break;
        
        case '\u0142' : // ł — LATIN SMALL LETTER L WITH STROKE
        case '\u1E37' : // ḷ — LATIN SMALL LETTER L WITH DOT BELOW
          output[outputPos++] = 'l';
          break;        
        
        case '\u00F1' : // ñ
        case '\u0148' : // ň — LATIN SMALL LETTER N WITH CARON
          output[outputPos++] = 'n';
          break;
        
        case '\u00F2' : // ò
        case '\u00F3' : // ó
        case '\u00F4' : // ô
        case '\u00F5' : // õ
        case '\u00F6' : // ö
        case '\u00F8' : // ø
        case '\u014D' : // ō — LATIN SMALL LETTER O WITH MACRON
		case '\u014F' : // ŏ — LATIN SMALL LETTER O WITH BREVE
		case '\u0151' : // ő — LATIN SMALL LETTER O WITH DOUBLE ACUTE
		case '\u01EB' : // ǫ — LATIN SMALL LETTER O WITH OGONEK
          output[outputPos++] = 'o';
          break;
        
        case '\u0161' : // š — LATIN SMALL LETTER S WITH CARON
        case '\u1E61' : // ṡ — LATIN SMALL LETTER S WITH DOT ABOVE
		case '\u1E63' : // ṣ — LATIN SMALL LETTER S WITH DOT BELOW
          output[outputPos++] = 's';
          break;
        
        case '\u1E6D' : // ṭ — LATIN SMALL LETTER T WITH DOT BELOW
          output[outputPos++] = 't';
          break;        
          
        //thorn, eth, wynn and yogh left as is.
		//case '\u00FE' : // þ
        //  output[outputPos++] = 't';
        //  output[outputPos++] = 'h';
        //  break;
        
        case '\u00F9' : // ù
        case '\u00FA' : // ú
        case '\u00FB' : // û
        case '\u00FC' : // ü
        case '\u0169' : // ũ — LATIN SMALL LETTER U WITH TILDE
		case '\u016B' : // ū — LATIN SMALL LETTER U WITH MACRON
		case '\u016D' : // ŭ — LATIN SMALL LETTER U WITH BREVE
		case '\u0217' : // ȗ — LATIN SMALL LETTER U WITH INVERTED BREVE
          output[outputPos++] = 'u';
          break;
          
		case '\u0175' : // ŵ — LATIN SMALL LETTER W WITH CIRCUMFLEX
          output[outputPos++] = 'w';
          break;
                    
        case '\u00FD' : // ý
        case '\u00FF' : // ÿ
        case '\u0233' : // ȳ — LATIN SMALL LETTER Y WITH MACRON
          output[outputPos++] = 'y';
          break;
          
        case '\u0153' : // œ
          output[outputPos++] = 'o';
          output[outputPos++] = 'e';
          break;

        case '\u017A': // z'
        case '\u017C' : // ż — LATIN SMALL LETTER Z WITH DOT ABOVE
        case '\u017E': // z caron
          output[outputPos++] = 'z';
          break;
          
        case '\uFB00': // ﬀ
            output[outputPos++] = 'f';
            output[outputPos++] = 'f';
            break;
            
        case '\uFB01': // ﬁ
            output[outputPos++] = 'f';
            output[outputPos++] = 'i';
            break;
            
        case '\uFB02': // ﬂ
            output[outputPos++] = 'f';
            output[outputPos++] = 'l';
            break;
            
        // following 2 are commented as they can break the maxSizeNeeded (and doing *3 could be expensive)
//        case '\uFB03': // ﬃ
//            output[outputPos++] = 'f';
//            output[outputPos++] = 'f';
//            output[outputPos++] = 'i';
//            break;
//        case '\uFB04': // ﬄ
//            output[outputPos++] = 'f';
//            output[outputPos++] = 'f';
//            output[outputPos++] = 'l';
//            break;

        case '\uFB05': // ﬅ
            output[outputPos++] = 'f';
            output[outputPos++] = 't';
            break;
            
        case '\uFB06': // ﬆ
            output[outputPos++] = 's';
            output[outputPos++] = 't';
          break;
          
        case '\u00D7' : // × — MULTIPLICATION SIGN
          output[outputPos++] = 'x';
          break;
          
        case '\u00F7' : // ÷ — DIVISION SIGN
          output[outputPos++] = '/';
          break;
        
        
        
        case '\u1F08' : // Ἀ — GREEK CAPITAL LETTER ALPHA WITH PSILI
		case '\u1F0C' : // Ἄ — GREEK CAPITAL LETTER ALPHA WITH PSILI AND OXIA
          output[outputPos++] = 'Α';
          break;
        
		case '\u1F18' : // Ἐ — GREEK CAPITAL LETTER EPSILON WITH PSILI
          output[outputPos++] = 'Ε';
          break;
        
        case '\u1F29' : // Ἡ — GREEK CAPITAL LETTER ETA WITH DASIA
          output[outputPos++] = 'Η';
          break;        
        
        case '\u1F38' : // Ἰ — GREEK CAPITAL LETTER IOTA WITH PSILI
		case '\u1F39' : // Ἱ — GREEK CAPITAL LETTER IOTA WITH DASIA
          output[outputPos++] = 'Ι';
          break;
        
        case '\u1FEC' : // Ῥ — GREEK CAPITAL LETTER RHO WITH DASIA
          output[outputPos++] = 'Ρ';
          break;
                          
        case '\u1F59' : // Ὑ — GREEK CAPITAL LETTER UPSILON WITH DASIA
          output[outputPos++] = 'Υ';
          break;
                            
        case '\u03AC' : // ά — GREEK SMALL LETTER ALPHA WITH TONOS
        case '\u1F00' : // ἀ — GREEK SMALL LETTER ALPHA WITH PSILI
		case '\u1F01' : // ἁ — GREEK SMALL LETTER ALPHA WITH DASIA
		case '\u1F03' : // ἃ — GREEK SMALL LETTER ALPHA WITH DASIA AND VARIA
		case '\u1F04' : // ἄ — GREEK SMALL LETTER ALPHA WITH PSILI AND OXIA
		case '\u1F05' : // ἅ — GREEK SMALL LETTER ALPHA WITH DASIA AND OXIA
		case '\u1F70' : // ὰ — GREEK SMALL LETTER ALPHA WITH VARIA
		case '\u1F85' : // ᾅ — GREEK SMALL LETTER ALPHA WITH DASIA AND OXIA AND YPOGEGRAMMENI
		case '\u1FB1' : // ᾱ — GREEK SMALL LETTER ALPHA WITH MACRON
		case '\u1FB3' : // ᾳ — GREEK SMALL LETTER ALPHA WITH YPOGEGRAMMENI
		case '\u1FB4' : // ᾴ — GREEK SMALL LETTER ALPHA WITH OXIA AND YPOGEGRAMMENI
		case '\u1FB6' : // ᾶ — GREEK SMALL LETTER ALPHA WITH PERISPOMENI
          output[outputPos++] = 'α';
          break;
          
        case '\u1F10' : // ἐ — GREEK SMALL LETTER EPSILON WITH PSILI
		case '\u1F11' : // ἑ — GREEK SMALL LETTER EPSILON WITH DASIA
		case '\u1F13' : // ἓ — GREEK SMALL LETTER EPSILON WITH DASIA AND VARIA
		case '\u1F14' : // ἔ — GREEK SMALL LETTER EPSILON WITH PSILI AND OXIA
		case '\u1F15' : // ἕ — GREEK SMALL LETTER EPSILON WITH DASIA AND OXIA
		case '\u1F72' : // ὲ — GREEK SMALL LETTER EPSILON WITH VARIA
		case '\u03AD' : // έ — GREEK SMALL LETTER EPSILON WITH TONOS
		  output[outputPos++] = 'ε';
		  break;

		case '\u1F20' : // ἠ — GREEK SMALL LETTER ETA WITH PSILI
		case '\u1F21' : // ἡ — GREEK SMALL LETTER ETA WITH DASIA
		case '\u1F22' : // ἢ — GREEK SMALL LETTER ETA WITH PSILI AND VARIA
		case '\u1F24' : // ἤ — GREEK SMALL LETTER ETA WITH PSILI AND OXIA
		case '\u1F25' : // ἥ — GREEK SMALL LETTER ETA WITH DASIA AND OXIA
		case '\u1F26' : // ἦ — GREEK SMALL LETTER ETA WITH PSILI AND PERISPOMENI
		case '\u1F27' : // ἧ — GREEK SMALL LETTER ETA WITH DASIA AND PERISPOMENI
		case '\u1F74' : // ὴ — GREEK SMALL LETTER ETA WITH VARIA
		case '\u1FC3' : // ῃ — GREEK SMALL LETTER ETA WITH YPOGEGRAMMENI
		case '\u1FC4' : // ῄ — GREEK SMALL LETTER ETA WITH OXIA AND YPOGEGRAMMENI
		case '\u1FC6' : // ῆ — GREEK SMALL LETTER ETA WITH PERISPOMENI
		case '\u1FC7' : // ῇ — GREEK SMALL LETTER ETA WITH PERISPOMENI AND YPOGEGRAMMENI
		case '\u03AE' : // ή — GREEK SMALL LETTER ETA WITH TONOS
		  output[outputPos++] = 'η';
		  break;
		
		case '\u0390' : // ΐ — GREEK SMALL LETTER IOTA WITH DIALYTIKA AND TONOS
		case '\u03AF' : // ί — GREEK SMALL LETTER IOTA WITH TONOS
		case '\u03CA' : // ϊ — GREEK SMALL LETTER IOTA WITH DIALYTIKA
		case '\u1F30' : // ἰ — GREEK SMALL LETTER IOTA WITH PSILI
		case '\u1F31' : // ἱ — GREEK SMALL LETTER IOTA WITH DASIA
		case '\u1F32' : // ἲ — GREEK SMALL LETTER IOTA WITH PSILI AND VARIA
		case '\u1F34' : // ἴ — GREEK SMALL LETTER IOTA WITH PSILI AND OXIA
		case '\u1F35' : // ἵ — GREEK SMALL LETTER IOTA WITH DASIA AND OXIA
		case '\u1F36' : // ἶ — GREEK SMALL LETTER IOTA WITH PSILI AND PERISPOMENI
		case '\u1F37' : // ἷ — GREEK SMALL LETTER IOTA WITH DASIA AND PERISPOMENI
		case '\u1F76' : // ὶ — GREEK SMALL LETTER IOTA WITH VARIA
		case '\u1FD0' : // ῐ — GREEK SMALL LETTER IOTA WITH VRACHY
		case '\u1FD6' : // ῖ — GREEK SMALL LETTER IOTA WITH PERISPOMENI
		  output[outputPos++] = 'ι';
		  break;
		  
		case '\u03CC' : // ό — GREEK SMALL LETTER OMICRON WITH TONOS
		case '\u1F40' : // ὀ — GREEK SMALL LETTER OMICRON WITH PSILI
		case '\u1F41' : // ὁ — GREEK SMALL LETTER OMICRON WITH DASIA
		case '\u1F42' : // ὂ — GREEK SMALL LETTER OMICRON WITH PSILI AND VARIA
		case '\u1F44' : // ὄ — GREEK SMALL LETTER OMICRON WITH PSILI AND OXIA
		case '\u1F45' : // ὅ — GREEK SMALL LETTER OMICRON WITH DASIA AND OXIA
		case '\u1F78' : // ὸ — GREEK SMALL LETTER OMICRON WITH VARIA
		  output[outputPos++] = 'ο';
		  break;
		
		case '\u1FE4' : // ῤ — GREEK SMALL LETTER RHO WITH PSILI
		case '\u1FE5' : // ῥ — GREEK SMALL LETTER RHO WITH DASIA    
		  output[outputPos++] = 'ρ';
		  break;
		
		case '\u03CB' : // ϋ — GREEK SMALL LETTER UPSILON WITH DIALYTIKA
		case '\u03CD' : // ύ — GREEK SMALL LETTER UPSILON WITH TONOS
		case '\u1F50' : // ὐ — GREEK SMALL LETTER UPSILON WITH PSILI
		case '\u1F51' : // ὑ — GREEK SMALL LETTER UPSILON WITH DASIA
		case '\u1F52' : // ὒ — GREEK SMALL LETTER UPSILON WITH PSILI AND VARIA
		case '\u1F53' : // ὓ — GREEK SMALL LETTER UPSILON WITH DASIA AND VARIA
		case '\u1F54' : // ὔ — GREEK SMALL LETTER UPSILON WITH PSILI AND OXIA
		case '\u1F55' : // ὕ — GREEK SMALL LETTER UPSILON WITH DASIA AND OXIA
		case '\u1F56' : // ὖ — GREEK SMALL LETTER UPSILON WITH PSILI AND PERISPOMENI
		case '\u1F7A' : // ὺ — GREEK SMALL LETTER UPSILON WITH VARIA
		case '\u1FE0' : // ῠ — GREEK SMALL LETTER UPSILON WITH VRACHY
		case '\u1FE1' : // ῡ — GREEK SMALL LETTER UPSILON WITH MACRON
		case '\u1FE6' : // ῦ — GREEK SMALL LETTER UPSILON WITH PERISPOMENI
		  output[outputPos++] = 'υ';
		  break;
		
		case '\u03D5' : // ϕ — GREEK PHI SYMBOL
		  output[outputPos++] = 'φ';
		  break;
		
		
		case '\u03CE' : // ώ — GREEK SMALL LETTER OMEGA WITH TONOS
		case '\u1F60' : // ὠ — GREEK SMALL LETTER OMEGA WITH PSILI
		case '\u1F61' : // ὡ — GREEK SMALL LETTER OMEGA WITH DASIA
		case '\u1F64' : // ὤ — GREEK SMALL LETTER OMEGA WITH PSILI AND OXIA
		case '\u1F65' : // ὥ — GREEK SMALL LETTER OMEGA WITH DASIA AND OXIA
		case '\u1F66' : // ὦ — GREEK SMALL LETTER OMEGA WITH PSILI AND PERISPOMENI
		case '\u1F7C' : // ὼ — GREEK SMALL LETTER OMEGA WITH VARIA
		case '\u1FA0' : // ᾠ — GREEK SMALL LETTER OMEGA WITH PSILI AND YPOGEGRAMMENI
		case '\u1FF3' : // ῳ — GREEK SMALL LETTER OMEGA WITH YPOGEGRAMMENI
		case '\u1FF4' : // ῴ — GREEK SMALL LETTER OMEGA WITH OXIA AND YPOGEGRAMMENI
		case '\u1FF6' : // ῶ — GREEK SMALL LETTER OMEGA WITH PERISPOMENI
		case '\u1FF7' : // ῷ — GREEK SMALL LETTER OMEGA WITH PERISPOMENI AND YPOGEGRAMMENI
		  output[outputPos++] = 'ω';
		  break;
		
		      
        case '\u02B9' : // ʹ — MODIFIER LETTER PRIME
		case '\u02BC' : // ʼ — MODIFIER LETTER APOSTROPHE
 		case '\u02BF' : // ʿ — MODIFIER LETTER LEFT HALF RING
 		case '\u02C8' : // ˈ — MODIFIER LETTER VERTICAL LINE
 		case '\u02C9' : // ˉ — MODIFIER LETTER MACRON
 		case '\u02D8' : // ˘ — BREVE
		case '\u0301' : // ́ — COMBINING ACUTE ACCENT
		case '\u0302' : // ̂ — COMBINING CIRCUMFLEX ACCENT
		case '\u0303' : // ̃ — COMBINING TILDE
		case '\u0304' : // ̄ — COMBINING MACRON
		case '\u0305' : // ̅ — COMBINING OVERLINE
		case '\u0306' : // ̆ — COMBINING BREVE
		case '\u0307' : // ̇ — COMBINING DOT ABOVE
		case '\u0313' : // ̓ — COMBINING COMMA ABOVE
		case '\u0314' : // ̔ — COMBINING REVERSED COMMA ABOVE
		case '\u0323' : // ̣ — COMBINING DOT BELOW
		case '\u0342' : // ͂ — COMBINING GREEK PERISPOMENI
		case '\u0345' : // ͅ — COMBINING GREEK YPOGEGRAMMENI
		case '\u035E' : // ͞ — COMBINING DOUBLE MACRON
		case '\u0375' : // ͵ — GREEK LOWER NUMERAL SIGN
		case '\u1FBD' : // ᾽ — GREEK KORONIS
          break;
          
        case '\u200A' : //   — HAIR SPACE
          output[outputPos++] = ' ';
          break;
          
		case '\u2012' : // ‒ — FIGURE DASH
		case '\u2013' : // – — EN DASH
		case '\u2014' : // — — EM DASH       
          output[outputPos++] = '-';
          break;
          
		case '\u2018' : // ‘ — LEFT SINGLE QUOTATION MARK
		case '\u2019' : // ’ — RIGHT SINGLE QUOTATION MARK
		case '\u201B' : // ‛ — SINGLE HIGH-REVERSED-9 QUOTATION MARK
          output[outputPos++] = '\'';
          break;
          
        case '\u201C' : // “ — LEFT DOUBLE QUOTATION MARK
		case '\u201D' : // ” — RIGHT DOUBLE QUOTATION MARK
          output[outputPos++] = '"';
          break;
          
        case '\u2025' : // ‥ — TWO DOT LEADER
		case '\u2026' : // … — HORIZONTAL ELLIPSIS
          output[outputPos++] = ' ';
          break;
                                              		        	
        default :
          output[outputPos++] = c;
          break;
        }
      }
    }
  }
}
