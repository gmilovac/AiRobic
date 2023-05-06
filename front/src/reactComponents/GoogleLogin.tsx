import React, { useEffect } from "react";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { GoogleLogin } from "@react-oauth/google";
import {Link, useNavigate} from "react-router-dom";
import { useGoogleLogin } from "@react-oauth/google";

function getCredentialResponse(credential: string | undefined): Promise<LoginResponse> {
  return new Promise((resolve, reject) => {
    fetch("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + credential)
        .then((response: Response) => response.json())
        .then((loginToken) => {
          if (!isLoginResponse(loginToken)) {
            reject(errLoginResponse)
          } else {
            resolve(loginToken)
          }
        }).catch(err => reject(errLoginResponse));
  })


}

function addUser(userID: string): Promise<boolean> {
  return new Promise((resolve, reject) => {
    fetch("http://localhost:3235/adduser?username=" + userID)
        .then((response: Response) => response.json())
        .then((addResponse) => {
          if (isAddUserResponse(addResponse) && addResponse.result == "success") {
            resolve(true)
          }
          else {
            reject(false)
          }
        }).catch(err => reject(false));
  })
}

function isNewUser(userID: string): Promise<boolean> {
  return new Promise((resolve, reject) => {
    fetch("http://localhost:3235/checkuser?username=" + userID)
        .then((userResponse: Response) => userResponse.json())
        .then((checkUser) => {
          if (!isCheckUserResponse(checkUser)) {
            console.log(checkUser)
            console.log("Invalid check user response");
            reject(false)
          } else {
            if (checkUser.message == "False") {
              console.log("User checked successfully does not exist");
              console.log("Proceed to register user");
              resolve(true);
            } else {
              console.log(checkUser)
              console.log("User exists");
              resolve(false);
            }
          }
        }).catch(err => reject(false));
  })
}
function Login(loginToken: LoginResponse) {
    localStorage.setItem("userID", loginToken.sub);
    console.log(loginToken);
    localStorage.setItem("givenName", loginToken.given_name);
    localStorage.setItem("loggedIn", "true");
    console.log(localStorage);
    window.location.href = "/";
}


export interface LoginResponse {
  sub: string;
  email: string;
  family_name: string;
  given_name: string;
}

const errLoginResponse = {
  sub: "ERROR",
  email: "ERROR",
  family_name: "ERROR",
  given_name: "ERROR"
}

interface CheckUserResponse {
  result: string;
  message: string;
}

interface AddUserResponse{
  result: string;
  message: string;
}

function isLoginResponse(rjson: any): rjson is LoginResponse {
  if (!("sub" in rjson) || !("email" in rjson)) return false;
  return true;
}

function isCheckUserResponse(rjson: any): rjson is CheckUserResponse {
  // if (!("result" in rjson)) {
  //   console.log("no result")
  // }
  // if (!("message" in rjson)) {
  //   console.log("no message")
  // }
  if (!("result" in rjson) || !("message" in rjson)) return false;
  return true;
}

function isAddUserResponse(rjson: any): rjson is AddUserResponse {
  if (!("result" in rjson) || !("message" in rjson)) return false;
  return true;
}



export { getCredentialResponse, isNewUser, isAddUserResponse, addUser, errLoginResponse, isCheckUserResponse, isLoginResponse, Login }


