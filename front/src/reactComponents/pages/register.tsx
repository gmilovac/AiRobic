import React, { useState } from "react";
import LoggedOutMenu from "../elements/loggedOutMenu";
import { GoogleLogin, GoogleOAuthProvider } from "@react-oauth/google";
import { Parallax } from "react-parallax";
import { Alert } from "@mui/material";
import {
  addUser,
  getCredentialResponse,
  isNewUser,
  Login,
} from "../GoogleLogin";
import { motion } from "framer-motion";

/**
 * Returns the Register page with google sign up button.
 * Using google Oauth2 the button authenticates the user using their google account and then will check with our
 * back-end if the user exists. If the user does not exist they will then be logged in and the user will be created in
 * our database. If there are any errors such as the user has already been registered, google authentication failing,
 * or backend is not running, a error alert will pop up on the screen detailing what went wrong
 * and the user will not be registered
 */

function Register() {
  const [submitIssue, setSubmitIssue] = useState(false);
  const [submitError, setSubmitError] = useState("");

  return (
    <Parallax
      bgImage={"/assets/images/GettyImages-1170269618-2.jpg"}
      strength={100}
      aria-label="Parallax image with register display page"
    >
      <LoggedOutMenu description={""} />
      <div className="register-window">
        <div className="content-wrapper">
          <motion.h1
            initial={{ opacity: 0, x: 70 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.0, duration: 0.5 }}
            style={{ color: "#ebe9e9" }}
          >
            Register
          </motion.h1>
          <motion.div
            initial={{ opacity: 0, x: 70 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.0, duration: 0.5 }}
          >
            <div className="sign-in-button">
              <GoogleOAuthProvider clientId="20770065062-amdbsjkao5gag2g7m0b7o4pn411akg80.apps.googleusercontent.com">
                <GoogleLogin
                  shape={"rectangular"}
                  size={"large"}
                  theme={"filled_black"}
                  width={"500"}
                  text={"signup_with"}
                  onSuccess={(credentialResponse) => {
                    getCredentialResponse(credentialResponse.credential)
                      .then((loginToken) => {
                        isNewUser(loginToken.sub)
                          .then((isNewUser) => {
                            if (isNewUser) {
                              addUser(loginToken.sub).then((userAdded) => {
                                if (userAdded) {
                                  Login(loginToken);
                                } else {
                                  setSubmitIssue(true);
                                  setSubmitError(
                                    "Error connecting to our server"
                                  );
                                }
                              });
                            } else {
                              setSubmitIssue(true);
                              setSubmitError(
                                "Email already registered, please sign in instead"
                              );
                            }
                          })
                          .catch((err) => {
                            setSubmitError("Error connecting to our server");
                            setSubmitIssue(true);
                          });
                      })
                      .catch((err) => {
                        setSubmitError("Error connecting to our server");
                        setSubmitIssue(true);
                      });
                  }}
                  onError={() => {
                    setSubmitIssue(true);
                    setSubmitError(
                      "Google authentication failed, please try again"
                    );
                  }}
                />

                {submitIssue && (
                  <div className="error-row">
                    <div
                      className="form-error-message"
                      aria-label="Error message"
                    >
                      <Alert
                        severity="error"
                        variant="filled"
                        sx={{ fontFamily: "Muli", fontSize: 14 }}
                        aria-label="Error: Workout plan submission unsuccessful"
                      >
                        {submitError}
                      </Alert>
                    </div>
                  </div>
                )}
              </GoogleOAuthProvider>
            </div>
          </motion.div>
        </div>
      </div>
    </Parallax>
  );
}

export default Register;
