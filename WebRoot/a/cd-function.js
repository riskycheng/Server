$(document).ready(function() {
    //返回查询
    $('#rs_btn').click(function(event) {
        $('#cal').animate({
                marginLeft: '0px'},
                2000, function() {
                $('#province').val('0');
                $('#city').val('0');
                $('#type').val('0');
                $('#period').val('0');
                $('#car').val('0');
                $('#price').val('');
                $('#pay').val('');
                $('#last').val('');
            });
    });
    //提交查询
    $('#cal_btn').click(function(event) {
        var _select = $('select'),
            _error = [];
        _select.each(function(index, el) {
            if ($(el).val() == '0' || $(el).val()=='-1') {
                _error.push('尚未选择' + $(el).find('option:eq(0)').html());
            }
        });
        //判断是否填写了价格首尾款
        if (isNaN($('#price').val()) || $('#price').val().replace(/\s+/,'')=='') {
            _error.push('车款价格不为数字');
        };
        if (isNaN($('#pay').val()) || $('#pay').val().replace(/\s+/,'')=='') {
            _error.push('首付款价格不为数字');
        };
        if ($('#type').val()=='3') {
            if (isNaN($('#last').val()) || $('#last').val().replace(/\s+/,'')=='') {
                _error.push('尾付款价格不为数字');
            };
            var _rate=$('#type').children('option:selected').attr('ref')=='1' ? 0.3 : 0.4;
            //首付款限制
            if (parseFloat(($('#pay').val())/parseFloat($('#price').val()))<_rate) {_error.push('首付款低于总价的'+(_rate*100)+'%')};
            if (parseFloat(($('#last').val())/parseFloat($('#price').val()))>_rate) {_error.push('尾付款高于总价的'+(_rate*100)+'%')};
        };
        if ($('#type').val()=='4') {
            //首付款限制
            if (($('#pay').val()/$('#price').val())<0.5) {_error.push('首付款低于总价的30%')};
        };
        if (parseFloat($('#pay').val())>parseFloat($('#price').val())) {
            _error.push('首付比例大于车辆价格');
        };
        if (parseFloat($('#last').val())>parseFloat($('#price').val())) {
            _error.push('首付比例大于车辆价格');
        };
        if (_error.length!=0) {
            alert('发生错误\n如下项目填写错误\n'+_error[0]);
            return false;
        };
        $.getJSON('x.php', {
            "all": $('#price').val(),
            "pay": $('#pay').val(),
            "last": $('#last').val(),
            "cate": $('#type').val(),
            "month": $('#period').val(),
            "type":"ckss",
            "key":"rs",
            "keyid":$('#province').val(),
            "pp":$('#city').val()
        }, function(json, textStatus) {
            //移动效果
            $('#cal').animate({
                marginLeft: '-'+_width+'px'},
                2000, function() {
                var _rate=parseFloat($('#pay').val())/parseFloat($('#price').val())*100;
                $('#rs_price').val($('#price').val());
                $('#rs_bank').val(parseFloat($('#price').val())-parseFloat($('#pay').val()));
                $('#rs_rate').val(_rate.toFixed(2)+'%');
                $('#rs_pay').val($('#pay').val());
                $('#rs_period').val($('#period').val()+'个月');
                $('#rs_month').val(json);
            });
        });
    });
    //绑定省份修改
    $('#province').change(function(event) {
        var _that = $(this);
        $.getJSON('x.php', {
            "type": "ckss",
            "key": "get_city",
            "keyid": $(this).val()
        }, function(json, textStatus) {
            var _option_list = [];
            for (i in json) {
                _option_list.push('<option value="' + json[i]['linkageid'] + '">' + json[i]['name'] + '</value>');
            }
            $('#city').html(_option_list.join(''));
        });
    });
    //绑定贷款类型修改
    $('#type').change(function(event) {
        var _that = $(this);
        switch (_that.val()) {
            case '0':
                $('#period').html('<option value="0">还款期数</option>');
                $('#pay').attr('placeholder', '首付款');
                $('#last').css('display', 'none');
                break;
            case '1':
                $('#period').html('<option value="0">还款期数</option><option value="12">12个月</value><option value="24">24个月</value><option value="36">36个月</value><option value="48">48个月</value><option value="60">60个月</value>');
                $('#pay').attr('placeholder', '首付款');
                $('#last').css('display', 'none');
                break;
            case '2':
                $('#period').html('<option value="0">还款期数</option><option value="12">12个月</value><option value="24">24个月</value><option value="36">36个月</value><option value="48">48个月</value><option value="60">60个月</value>');
                $('#pay').attr('placeholder', '首付款');
                $('#last').css('display', 'none');
                break;
            case '3':
                var _refs = _that.children('option:selected').attr('ref');
                if (_refs == 1) {
                    $('#period').html('<option value="0">还款期数</option><option value="36">36个月</value>');
                    $('#pay').attr('placeholder', '首付款不低于车辆价格的30%');
                    $('#last').attr('placeholder', '尾付款不高于车辆价格的30%').css('display', 'block');
                } else {
                    $('#period').html('<option value="0">还款期数</option><option value="24">24个月</value>');
                    $('#pay').attr('placeholder', '首付款不低于车辆价格的40%');
                    $('#last').attr('placeholder', '尾付款不高于车辆价格的40%').css('display', 'block');
                }
                break;
            case '5':
                $('#period').html('<option value="0">还款期数</option><option value="12">12个月</value><option value="24">24个月</value><option value="36">36个月</value><option value="48">48个月</value><option value="60">60个月</value>');
                $('#pay').attr('placeholder', '首付款');
                $('#last').css('display', 'none');
                break;
            case '4':
                $('#period').html('<option value="0">还款期数</option><option value="12">12个月</value>');
                $('#pay').attr('placeholder', '首付款不低于车辆价格的50%');
                break;
        }
    });
    //获取省份
    $.getJSON('x.php', {
        "type": "ckss",
        "key": "get_province"
    }, function(json, textStatus) {
        var _option_list = [];
        for (i in json) {
            _option_list.push('<option value="' + i + '">' + json[i] + '</value>');
        }
        $('#province').append(_option_list.join(''));
        //设默认值
        $('#province').find('option[value="3695"]').attr('selected', 'selected');
        $('#province').change();
    });
    //获取汽车品牌
    $.getJSON('x.php', {
        "type": "getInfo",
        "key": "car_list"
    }, function(json, textStatus) {
        var _option_list = [];
        for (i in json) {
            _option_list.push('<option value="-1" style="background-color:#e3e3e3;">' + i + '</value>');
            for (k in json[i]) {
                _option_list.push('<option value="' + json[i][k]['value'] + '">'+ i+'-' + json[i][k]['text'] + '</value>');
            }
        }
        $('#car').append(_option_list.join(''));
        $( "#car" ).combobox();
    });
});