/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.googleauthentication2fa;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.is4103.matchub.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ngjin
 */
@Slf4j
@RestController
@RequiredArgsConstructor
//@RequestMapping("/code")
@RequestMapping("/authenticated")
public class GoogleAuthenticationController {

    private final GoogleAuthenticator gAuth;

    @Autowired
    private UserService userService;

    @SneakyThrows
    @GetMapping("/generateQRCode/{email}")
    public void generateQRCode(@PathVariable String email, HttpServletResponse response) {
        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);

        // generate QRCode on backend site
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("matchub", email, key);
        System.out.println(otpAuthURL);

        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        //Simple writing to outputstream 
        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.close();
    }

    @PostMapping("/validate2FAKey")
    public Boolean validateKey(@RequestBody Validate2FAVO body) {
        Boolean valid2fa = gAuth.authorizeUser(body.getEmail(), body.getCode());
        userService.set2FAValidity(body.getEmail(), valid2fa);
        return valid2fa;

    }
}
