(ns mangosteen.core
  (:require [clojure.pprint :refer [pprint]]
            [clojure.zip :as zip]))

(defn- parse-int [str]
  (try
    (Long/parseLong str)
    (catch NumberFormatException _ nil)))

(defn- structural-zip [root]
  (zip/zipper #(or (set? %) (map? %) (seq? %) (vector? %))
              seq
              (fn [node children]
                (cond
                 (or (vector? node) (set? node) (map? node))
                   (with-meta (into (empty node) children) (meta node))
                 :else
                   (with-meta (seq children) (meta node))))
              root))

(defn- navigate [zipper ^String input]
  (or
   (if-let [n (parse-int input)]
     (when (>= n 0)
       (reduce #(%2 %1) (zip/down zipper) (repeat n zip/right)))
     (case (first input)
       (\u \U) (zip/up zipper)
       nil))
   zipper))

(defn- print-children [x]
  (if (coll? x)
    (doseq [[item i] (map vector x (iterate inc 0))]
      (printf "%d: %s\n" i (pr-str item)))
    (println "... is atomic")))

(defn- print-with-neighborhood [x & [parent]]
  (pprint x)
  (printf "... of class %s\n" (pr-str (class x)))

  (when parent
    (printf "\n... parent is: %s\n" parent))

  (println)
  (print-children x))

(defn- print-bar [& [n-char]]
  (let [n-char (or n-char 75)]
    (dotimes [i n-char] (print \-))
    (println)))

(defn- prompt [prompt-string & [accept]]
  (print prompt-string)
  (flush)
  (let [res (read-line)]
    (println)
    res))

(defn- explore-core [root]
  (loop [loc root]
    (println)
    (print-bar)
    (println "CURRENT FORM: ")
    (print-with-neighborhood (zip/node loc)
                             (when-let [it (zip/up loc)]
                               (zip/node it)))
    (println)
    (println "(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.")
    (let [user (prompt "Your choice? ")]
      (case user
        ("q" "Q") [:quit]
        ("s" "S") [:select loc]
        (recur (navigate loc user))))))

(defn explore [x]
  (let [[tag arg1] (explore-core (structural-zip x))]
    (condp = tag
      :quit   :quit
      :select [:select (zip/node arg1)])))

(defn explore-and-edit-once [x edit-fn]
  (let [[tag arg1] (explore-core (structural-zip x))]
    (condp = tag
      :quit   x
      :select (zip/root (zip/edit arg1 edit-fn)))))

(defn interact [x]
  (let [root (structural-zip x)]
    (loop [loc root]
      (let [[tag arg1] (explore-core loc)]
        (condp = tag
          :quit   (zip/root loc)
          :select (let [new-loc (zip/edit arg1 (fn [x]
                                                 (read-string (prompt "Edit to what? "))))]
                    (recur new-loc)))))))
