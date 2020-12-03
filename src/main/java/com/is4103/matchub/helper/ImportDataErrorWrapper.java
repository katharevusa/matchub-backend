/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.helper;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngjin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportDataErrorWrapper {

    private int numSuccesses;

    private int numFailures;

    private List<String> errorMessages = new ArrayList<>();
}
