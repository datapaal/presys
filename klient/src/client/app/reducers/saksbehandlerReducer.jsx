import jwtDecode from 'jwt-decode';

import { LOGIN_SUCCESS, LOGOUT_SUCCESS } from 'constants/actionTypes';


const defaultState = {
  loggedIn: false,
  profile: null,
};

export default function saksbehandlerReducer(state = defaultState,
  action = null) {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        loggedIn: true,
        profile: jwtDecode(action.payload.token),
      };
    case LOGOUT_SUCCESS:
      return {
        loggedIn: false,
        profile: null,
      };
    default:
      return state;
  }
}
