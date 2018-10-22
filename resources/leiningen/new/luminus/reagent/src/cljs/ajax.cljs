(ns <<project-ns>>.ajax
  (:require [ajax.core :as ajax]
            [luminus-transit.time :as time]
            [cognitect.transit :as transit]<% if re-frame %>
            [re-frame.core :as rf]<% endif %>))

(defn local-uri? [{:keys [uri]}]
  (not (re-find #"^\w+?://" uri)))

(defn default-headers [request]
  (if (local-uri? request)
    (-> request<% if servlet %>
        (update :uri #(str js/context %))<% endif %>
        (update :headers #(merge {"x-csrf-token" js/csrfToken} %)))
    request))

;; injects transit serialization config into request options
(defn as-transit [opts]
  (merge {:raw             false
          :format          :transit
          :response-format :transit
          :reader          (transit/reader :json time/time-deserialization-handlers)
          :writer          (transit/writer :json time/time-serialization-handlers)}
         opts))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))
<% if re-frame %>
(def http-methods
  {:get    ajax/GET
   :post   ajax/POST
   :put    ajax/PUT
   :delete ajax/DELETE})

(rf/reg-fx
  :http
  (fn [{:keys [method
               url
               success-event
               error-event
               params
               ajax-map]
        :or   {error-event [:common/set-error]
               ajax-map    {}}}]
    ((http-methods method)
      url (merge
            {:params        params
             :handler       (fn [response]
                              (when success-event
                                (rf/dispatch (conj success-event response))))
             :error-handler (fn [error]
                              (rf/dispatch (conj error-event error)))}
            ajax-map))))
<% endif %>
