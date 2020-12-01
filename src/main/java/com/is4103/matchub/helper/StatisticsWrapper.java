package com.is4103.matchub.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tjle2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsWrapper {

    private String[] months;
    private Object[] values;
}
