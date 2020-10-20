/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.vo;

import com.is4103.matchub.entity.ReviewEntity;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author ngjin
 */
@Data
public class ReviewCreateVO {

    @NotNull(message = "Review content can not be null.")
    @NotBlank(message = "Review content can not be null.")
    private String content;

    @NotNull(message = "Review rating can not be null.")
    private BigDecimal rating;

    @NotNull(message = "Project id can not be null.")
    private Long projectId;

    @NotNull(message = "Reviewer id can not be null.")
    private Long reviewerId;

    @NotNull(message = "Review receiver id can not be null.")
    private Long reviewReceiverId;

    public void updateReview(ReviewEntity newReview) {
        newReview.setContent(this.content);
        newReview.setRating(this.rating);

        //project, reviewer & reviewReceiver associations are set in Service class
    }

}
