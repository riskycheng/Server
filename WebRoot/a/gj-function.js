$(document).ready(function() {

    //提交查询
    $('#cal_btn').click(function(event) {
        var _select = $('select'),
            _error = [];
        _select.each(function(index, el) {
            if ($(el).val() == '0' || $(el).val()=='-1') {
                if (!$(el).find('option:selected').attr('error')) {
                    _error.push('尚未选择' + $(el).find('option:eq(0)').html());
                }
                else {
                    _error.push('尚未选择' + $(el).find('option:selected').attr('error'));
                }
            }
        });
        if(isNaN($('#kilometer').val()) || $('#kilometer').val().replace(/\s+/,'')==''){
            _error.push('请输入数字型的行驶里程');
        }
        if (_error.length!=0) {
            alert('发生错误\n如下项目填写错误\n'+_error[0]);
            return false;
        };
        $.post('x.php?type=getInfo&key=result', $('form').serialize(), function(data, textStatus, xhr) {
            if (data=='0') {
                alert('您选择的车辆过于冷门无法估价');
            }
            else {
                $('#cal').animate({
                    marginLeft: '-'+_width+'px'},
                    2000, function() {
                    var _rate=parseFloat($('#pay').val())/parseFloat($('#price').val())*100;
                    $('#rs_brand').val($('#brand_name').val());
                    $('#rs_series').val($('#series_name').val());
                    $('#rs_model').val($('#model_name').val());
                    $('#rs_run').val($('#kilometer').val());
                    $('#rs_price').val(data);
                });
            }
        });
    });

    $('#rs_btn').click(function(event) {
        $('#cal').animate({
                marginLeft: '0px'},
                2000, function() {
                $('#province').val('0');
                $('#city').val('0');
                $('#brand_id').val('0');
                $('#series_id').val('0');
                $('#model_id').val('0');
                $('#year').val('');
                $('#month').val('');
                $('#kilometer').val('');
            });
    });
    
    //绑定省份修改
    $('#province').change(function(event) {
        var _that = $(this);
        $.getJSON('x.php', {
            "type": "getInfo",
            "key": "get_city",
            "province_id": $(this).val()
        }, function(json, textStatus) {
            var _option_list = [];
            for (i in json) {
                _option_list.push('<option value="' + i + '">' + json[i] + '</value>');
            }
            $('#city').html(_option_list.join(''));
            $('#city_name').val($('#city').children('option:eq(0)').html());
            $('#province_name').val($('#province').children('option:selected').html());
        });
    });

    //绑定品牌修改
    $('#brand_id').change(function(event) {
        $.getJSON('x.php',{
            "type": "getInfo",
            "key": "car_series",
            "brand_id":$(this).val()        
        }, function(json, textStatus) {
            var _option_list = [];
            for (i in json) {
                _option_list.push('<option value="' + json[i]['value'] + '">' + json[i]['text'] + '</value>');
            }
            $('#series_id').html(_option_list.join(''));
            $('#series_id').val($('#brand_id').children('option:selected').val());
            $('#series_id').change();
            $('#series_name').val($('#series_id').children('option:eq(0)').html());
            $('#brand_name').val($('#brand_id').children('option:selected').html());
        });
    });

    //绑定车模型修改
    $('#model_id').change(function(event) {
        $('#model_name').val($('#model_id').children('option:selected').html());
    });

    //绑定车型修改
    $('#series_id').change(function(event) {
        $.getJSON('x.php',{
            "type": "getInfo",
            "key": "car_model",
            "series_id":$(this).val()        
        }, function(json, textStatus) {
            var _option_list = [];
            for (i in json) {
                if (i!='#') {
                    _option_list.push('<option value="-1" style="background-color:#e3e3e3;" error="请选择车型">' + i + '</value>');
                };
                for(k in json[i]){
                    _option_list.push('<option value="' + json[i][k]['value'] + '">' + json[i][k]['text'] + '</value>');
                }
            }
            $('#model_id').html(_option_list.join(''));
            $('#model_name').val($('#model_id').children('option:eq(0)').html());
            $('#series_name').val($('#series_id').children('option:selected').html());
        });
    });

    //绑定城市修改
    $('#city').change(function(event) {
        $('#city_name').val($('#city').children('option:selected').html());
    });
    
    //获取省份
    $.getJSON('x.php', {
        "type": "getInfo",
        "key": "province"
    }, function(json, textStatus) {
        var _option_list = [];
        for (i in json) {
            _option_list.push('<option value="' + i + '">' + json[i] + '</value>');
        }
        $('#province').append(_option_list.join(''));
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
                _option_list.push('<option value="' + json[i][k]['value'] + '">' + json[i][k]['text'] + '</value>');
            }
        }
        $('#brand_id').append(_option_list.join(''));
    });
});