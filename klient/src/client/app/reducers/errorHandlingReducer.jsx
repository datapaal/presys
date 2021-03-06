import * as types from 'constants/actionTypes';

function extractErrorMessage(data) {
  if (data.response && data.response.data) {
    if (data.response.data.message) {
      return `errorcode.${data.response.data.message}`;
    }
    if (data.response.data.entity) {
      return data.response.data.entity;
    }
  }
  if (data.message) {
    return data.message;
  }

  return data;
}

export default function errorHandlingReducer(state = {
  errorMessage: '',
},
  action = null) {
  switch (action.type) {
    case types.SHOW_ERROR_MESSAGE:
      return Object.assign({ }, state, { errorMessage: extractErrorMessage(action.data) });
    case types.REMOVE_ERROR_MESSAGE:
      return Object.assign({ }, state, { errorMessage: '' });
    default:
      if (typeof action.error !== 'undefined') {
        return {
          ...state,
          errorMessage: action.error.statusText,
        };
      }
      return state;
  }
}
