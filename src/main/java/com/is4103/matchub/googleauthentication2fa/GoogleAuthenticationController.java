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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

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
    @RequestMapping(method = RequestMethod.GET, value = "/generateQRCodeImage/{email}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] generateQRCodeImage(@PathVariable String email, HttpServletResponse response) throws Exception {
        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("matchub", email, key);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        //convert buffered image into btye array 
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;

    }

    @SneakyThrows
    @RequestMapping(method = RequestMethod.GET, value = "/generateSecret/{email}")
    public String generateSecret(@PathVariable String email, HttpServletResponse response) throws Exception {
        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("matchub", email, key);
        int startIndex = 7 + otpAuthURL.lastIndexOf("secret=");
        int endIndex = otpAuthURL.indexOf("&");
        String secret = otpAuthURL.substring(startIndex, endIndex);

        System.out.println(secret);
        return secret;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validate2FAKey")
    public Boolean validateKey(@RequestBody Validate2FAVO body) {
        Boolean valid2fa = gAuth.authorizeUser(body.getEmail(), body.getCode());
        userService.set2FAValidity(body.getEmail(), valid2fa);
        return valid2fa;
    }
    
    //    @SneakyThrows
//    @GetMapping("/generateQRCode/{email}")
//    public void generateQRCode(@PathVariable String email, HttpServletResponse response) {
//        final GoogleAuthenticatorKey key = gAuth.createCredentials(email);
//
//        // generate QRCode on backend site
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("matchub", email, key);
//        System.out.println(otpAuthURL);
//
//        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
//
//        //Simple writing to outputstream 
//        ServletOutputStream outputStream = response.getOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
//        outputStream.close();
//    }
}
