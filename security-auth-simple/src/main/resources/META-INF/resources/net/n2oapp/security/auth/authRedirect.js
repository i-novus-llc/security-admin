/*global define, require, console */
define([
    'jquery',
    'backbone'
], function ($, Backbone) {
    "use strict";
    return Backbone.View.extend({
        initialize: function (options) {
            var uiAuth = this;
            /**
             * Wrapped for checked function
             * @type {*|function(this:*)}
             */
            Backbone.ajax = function (settings) {
                var def = $.Deferred();
                def.$xhr = Backbone.$.ajax
                    .apply(Backbone.$, arguments)
                    .done(def.resolve)
                    .fail(function (xhr) {
                        if (uiAuth.handlerAjaxError(xhr)) {
                            window.location.replace(options.pathToRedirect);
                        }
                        def.reject.apply(def, arguments);
                    });
                return def;
            }.bind(this);
        },
        handlerAjaxError: function (xhr) {
            return xhr.status === 401;
        }
    });
});