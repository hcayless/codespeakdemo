(ns codespeak.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File FileInputStream)
           (java.text Normalizer)
           (java.util Arrays)
           (javax.xml.parsers SAXParserFactory)
           (org.xml.sax InputSource)
           (org.xml.sax.helpers DefaultHandler)))

(def chrs (atom ""))

(defn lpad
  "Pad the input string with zeroes"
  [string length]
  (if (< (count string) length)
    (let [s (StringBuilder.)]
      (dotimes [n (- length (count string))]
        (.append s "0"))
      (str s string))
    string))

(defn attrseq
  "Turn a seq from an org.xml.sax Attributes object"
  [atts]
    (loop [i (range (.getLength atts)) result nil]
      (if (empty? i)
        result
        (recur (rest i) 
               (conj result {:ns (.getURI atts (first i)), 
                             :name (.getLocalName atts (first i)),
                             :value (.getValue atts (first i))})))))

;; An org.xml.sax.helpers.DefaultHandler that visualizes the process
;; of parsing an XML document with SAX.
(def handler
  (proxy [DefaultHandler] []
    (characters [ch start length]
      (when (not (Arrays/equals (.toCharArray @chrs) ch))
        (println (str "New buffer: <<<<<<<<<<\n" (String. ch) "\n>>>>>>>>>>"))
        (reset! chrs (String. ch))
        (Thread/sleep 1000))
      (println (str "Characters from " start " to " (+ start length) ": \"" (String. ch start length) "\" of the buffer,"))
      (Thread/sleep 100))
    (endDocument []
      (println "End of the document."))
    (endElement [uri localName qName]
      (println (str "</" localName ">"))
      (Thread/sleep 100))
    (startDocument []
      (println "Start of the document."))
    (startElement [uri localName qName attributes]
      (print (str "<" localName ">"))
      (let [atts (attrseq attributes)]
        (if (not (nil? atts))
          (println (str ", \nAttributes" (reduce (fn [a b] (str a ", " (:name b) ": " (:value b))) (cons "" (attrseq attributes)))))
          (println)))
      (Thread/sleep 100))))

(defn sax-parse
  "Parse an XML file using the handler above."
  [file]
  (try
    (let [pfactory (SAXParserFactory/newInstance)]
      (.setNamespaceAware pfactory true)
      (doto (.newSAXParser pfactory) 
        (.parse (InputSource. (FileInputStream. file)) 
        handler)))
    (catch Exception e
      (println (.getMessage e)))))

(defn read-bytes
  "Read a file and print the bytes read."
  [file]
  (with-open [stream (io/input-stream file)]
    (loop [b (.read stream)]
      (when (not= b nil)
        (println (str (lpad (Integer/toBinaryString b) 8) " "))
        (Thread/sleep 100)
        (recur (.read stream))))))

(defn read-chars
  "Read a file and print the characters read."
  [file]
  (with-open [rdr (io/reader file)]
    (loop [b (.read rdr)]
      (when (not= b nil)
        (println (str (Character/toString (char b)) " "))
        (Thread/sleep 100)
        (recur (.read rdr))))))

(defn read-lines
  "Read a file and print the lines read."
  [file]
  (with-open [rdr (io/reader file)]
    (let [lines (line-seq rdr)]
      (doseq [line lines]
        (println line)
        (Thread/sleep 100)))))

(defn read-tokens
  "Read a file, tokenizing the words and print the token stream."
  [file]
  (with-open [rdr (io/reader file)]
    (let [lines (line-seq rdr)]
      (doseq [line lines]        
        (doseq [token (str/split line #"[- ',._#():\[\];*]+")]
          (when (not (str/blank? line))
            (println (str "\"" token "\""))
            (Thread/sleep 100)))))))
