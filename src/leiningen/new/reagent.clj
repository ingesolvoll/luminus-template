(ns leiningen.new.reagent
  (:require [leiningen.new.common :refer :all]))

(def reagent-assets
  [["{{client-path}}/{{sanitized}}/core.cljs" "reagent/src/cljs/core.cljs"]
   ["{{client-path}}/{{sanitized}}/ajax.cljs" "reagent/src/cljs/ajax.cljs"]])

(defn reagent-dependencies [{:keys [features]}]
  [['reagent "1.0.0"]
   ['cljs-ajax "0.8.1"]])

(defn reagent-features [[assets options :as state]]
  (if (some #{"+reagent"} (:features options))
    [(into (remove-conflicting-assets assets "core.cljs") reagent-assets)
     (-> options
         (assoc :reagent true)
         (append-options :dependencies (reagent-dependencies options)))]
    state))
