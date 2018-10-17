(ns <<project-ns>>.routing
  (:require
    [re-frame.core :as rf]
    [reitit.core :as reitit])
  (:import goog.History))

(def routes
  [["/" :home]
   ["/about" :about]])

(rf/reg-sub
  :nav/route
  :<- [:kee-frame/route]
  identity)

(rf/reg-sub
  :nav/page
  :<- [:nav/route]
  (fn [route _]
    (-> route :data :name)))