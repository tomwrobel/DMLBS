package org.exist.indexing.lucene;

/**
 * Copyright 2004 The Apache Software Foundation
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

/** Based on ISOLatin1AccentAnalyzer by Mike Sokolov, generous thanks go
 *  to him. Edited by Tom Wrobel (thomas.wrobel@classics.ox.ac.uk)
**/

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import java.io.Reader;
import java.util.Set;

/**
 * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link
 * LowerCaseFilter}, {@link DiacriticFilter} and {@link StopFilter}.
*/
public class DMLBSAccentAnalyzer extends Analyzer {
  private Set<String> stopSet;

  /** An array containing some common English words that are usually not
  useful for searching. */
  public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;

  /** Builds an analyzer. */
  public DMLBSAccentAnalyzer() {
    this(STOP_WORDS);
  }

  /** Builds an analyzer with the given stop words. */
  public DMLBSAccentAnalyzer(String[] stopWords) {
    stopSet = StopFilter.makeStopSet(stopWords);
  }

  /** Constructs a {@link StandardTokenizer} filtered by a {@link
  StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}. */
  public TokenStream tokenStream(String fieldName, Reader reader) {
    TokenStream result = new StandardTokenizer(reader);
    result = new StandardFilter(result);
    result = new LowerCaseFilter(result);
    result = new org.exist.indexing.lucene.DMLBSAccentFilter(result);
    //result = new ASCIIFoldingFilter(result);
    result = new StopFilter(result, stopSet);
    return result;
  }
}
