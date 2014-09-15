var TBSCrmTool = {
        init: function (token, callback) {
            var self = this;
            $.ajax({
                type: "get",
                url: "http://crm.tbs-info.com/api/v1/activities",
                data: {
                    token: token
                },
                success: function (data) {
                    self.activity = data;

                    callback(data);
                },
                error: function () {
                    console.log("Initialize error, plz check token.");
                },
                dataType: "json",
                crossDomain: true
            });

            // 記錄 sessions
            $.ajax({
                type: "post",
                url: "http://crm.tbs-info.com/api/v1/sessions",
                data: {
                    token: token
                },
                crossDomain: true
            });
        },
        initLoginBtn: function (type, domId) {
            var self = this;

            if (type == 'sina') {
                $('#' + domId).click(function () {
                    self._loginToWeibo(function () {
                        // nothing
                    });
                });
            }
        },
        initShareBtn: function (type, domId, shareMessage, success) {
            var self = this;

            if (type == 'sina') {
                $('#' + domId).click(function () {
                    self._loginToWeibo(function () {
                        self._weiboShareMessage(shareMessage, success);
                    });
                });
            }
        },
        initRegisterForm: function (domId) {

        },
        shareMessage: function (type, messageData, success, error) {
            if (type == 'sina') {
                this._weiboShareMessage(messageData, success, error);
            }
        },
        addConsumer: function (consumerData, success, error) {
            if (this.activity) {
                consumerData.token = this.activity.token;

                $.ajax({
                    type: "post",
                    url: "http://crm.tbs-info.com/api/v1/consumers",
                    data: consumerData,
                    success: function (data) {
                        console.log(data);
                        success(data);
                    },
                    dataType: "json",
                    crossDomain: true
                });
            } else {
                error();
            }
        },
        getConsumer: function (consumerData, success, error) {
            if (this.activity) {
                consumerData.token = this.activity.token;

                $.ajax({
                    type: "get",
                    url: "http://crm.tbs-info.com/api/v1/consumers",
                    data: consumerData,
                    success: function (data) {
                        console.log(data);
                        success(data);
                    },
                    error: function () {
                        error();
                    },
                    dataType: "json",
                    crossDomain: true
                });
            } else {
                error();
            }
        },

        _getWeiboAuthUrl: function () {
            return 'https://api.weibo.com/oauth2/authorize?' + $.param({
                client_id: this.activity.sina_client_id,
                state: this.activity.token,
                response_type: 'code',
                redirect_uri: 'http://crm.tbs-info.com/redirect_pages/sina'
            });
        },
        _loginToWeibo: function (callback) {
            var self = this;

            $.fancybox.open(
                {
                    href: self._getWeiboAuthUrl()
                },
                {
                    type: 'iframe',
                    fitToView: true,
                    autoSize: true
                }
            );
            window.addEventListener('message', function (e) {
                self.sina_token = JSON.parse(e.data);
                $.fancybox.close();

                console.log(self.sina_token);

                callback();
            }, false);
        },
        _weiboShareMessage: function (messageData, success, error) {
            if (this.sina_token) {
                $.ajax({
                    type: "post",
                    url: "http://crm.tbs-info.com/proxies/sina_post",
                    data: {
                        access_token: this.sina_token.access_token,
                        status: messageData
                    },
                    success: function (data) {
                        console.log(data);
                        success(data);
                    },
                    dataType: "json",
                    crossDomain: true
                });
            } else {
                error();
            }
        },

        DEFAULT: {
            HOST: 'crm.tbs-info.com'
        }
    }
    ;
