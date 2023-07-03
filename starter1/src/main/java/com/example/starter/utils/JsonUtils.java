package com.example.starter.utils;

import com.example.starter.model.exceptions.ErrorCodeEnum;
import com.example.starter.model.exceptions.JSONException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;


import java.text.SimpleDateFormat;
import java.util.List;

public class JsonUtils {
	@SuppressWarnings("unchecked")
	public static Object readValue(String json, Class type, String where) {
		Object o = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			o = mapper.readValue(json, type);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
		return o;
	}

	public static String writeValueAsString(Object obj, String where) {
		if(obj == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
	}

	public static String writeJson(Object obj, String where) {
		if(obj == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Object readJson(String json, Class type, String where) {
		Object o = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
			o = mapper.readValue(json, type);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
		return o;
	}

  public static <T> T readValue_T(Object obj, Class<T> valueType, String where) {
    T o = null;
    try {
      ModelMapper modelMapper = new ModelMapper();
      modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
      o = modelMapper.map(obj, valueType);
    } catch (Exception e) {
      throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
    }
    return o;
  }

	public static <T> T readValue_T(String json, Class<T> valueType, String where) {
		T o = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			o = mapper.readValue(json, valueType);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
		return o;
	}

	public static <T> T readValue_T(String json, Class<T> valueType, String where, String dateFormat) {
		T o = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat(dateFormat));
			o = mapper.readValue(json, valueType);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
		return o;
	}

	public static <T> String writeValueAsString_T(T obj, String where) {
		if(obj == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
	}

	public static <T> String writeValueAsString_T(T obj, String where, String dateFormat) {
		if(obj == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat(dateFormat));
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
	}

	public static <T> String writeValueAsString_T(List<T> ls, String where) {
		if(ls == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
			return mapper.writeValueAsString(ls);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
	}

	public static <T> String writeValueAsString_T(List<T> ls, String where, String dateFormat) {
		if(ls == null)
			return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setDateFormat(new SimpleDateFormat(dateFormat));
			return mapper.writeValueAsString(ls);
		} catch (Exception e) {
			throw new JSONException(ErrorCodeEnum.JSON_ERROR.get(where, e.getMessage()), e);
		}
	}

  public static boolean isValidJson(String json) {
    try {
      new JSONObject(json);
    } catch (org.json.JSONException e) {
      try {
        new JSONArray(json);
      } catch (org.json.JSONException ne) {
        return false;
      }
    }
    return true;
  }
}
