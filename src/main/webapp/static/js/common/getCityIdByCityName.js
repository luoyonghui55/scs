/**
 * 通过城市名获取城市编号
 */
function getCityIdByName(cityName) {
	$.ajax({
		type: 'post',
		url: 'address/queryCityIdByName',
		dataType: 'json',
		async: false,
		timeout: 6000,
		data: {cityName: cityName},
		success: function(result) {
			if (result != null && result != '') {
				localStorage.setItem(LAST_CITY_ID_KEY, result.cityid);
				localStorage.setItem(LAST_CITY_NAME_KEY, cityName);
			}
		}
	});
}