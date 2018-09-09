/**
 * 常量类
 */
var currentPath = window.document.location.href;
var pathName = window.document.location.pathname;
var pathIndexOf = currentPath.indexOf(pathName);
var localhostPaht = currentPath.substring(0, pathIndexOf);
var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
var SCS_INDEX_URL = localhostPaht + projectName + '/';//项目根路径

//上一次城市信息存储的KEY
var LAST_CITY_ID_KEY = 'LAST_CITY_ID_KEY', LAST_CITY_NAME_KEY = 'LAST_CITY_NAME_KEY', ALL_AREA_KEY = 'ALL_AREA_KEY',LAST_LAT = 'LAST_LAT', LAST_LNG = 'LAST_LNG', SERVICE_TYPE_KEY_LIST = 'SERVICE_TYPE_KEY_LIST',SERVICE_TYPE_VALUE_LIST = 'SERVICE_TYPE_VALUE_LIST';
//当前城市编号和名称，默认城市编号和名称
var currentCityId = '', currentCityName = '', defaultCityId = '110100', defaultCityName = '北京市', last_city_id, last_city_name, SCS_USER_NAME = 'SCS_USER_NAME',SCS_USER_PHONE = 'SCS_USER_PHONE',SCS_USER_ID = 'SCS_USER_ID';