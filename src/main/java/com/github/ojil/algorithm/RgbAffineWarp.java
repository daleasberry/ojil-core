/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 * 
 *  You should have received a copy of the Lesser GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ojil.algorithm;

import com.github.ojil.core.Gray8OffsetImage;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbOffsetImage;
import com.github.ojil.core.Vec2;

/**
 * Copyright 2008 by Jon A. Webb
 * 
 * @author webb
 */
public class RgbAffineWarp extends PipelineStage {
    private final Gray8AffineWarp grayWarp;
    private final RgbSelectGray selectRed, selectGreen, selectBlue;
    
    public RgbAffineWarp(final Integer[][] warp) throws ImageError {
        grayWarp = new Gray8AffineWarp(warp);
        selectRed = new RgbSelectGray(RgbSelectGray.RED);
        selectGreen = new RgbSelectGray(RgbSelectGray.GREEN);
        selectBlue = new RgbSelectGray(RgbSelectGray.BLUE);
    }
    
    @Override
    public void push(final Image<?> imageInput) throws ImageError {
        if (!(imageInput instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, imageInput.toString(), null, null);
        }
        selectRed.push(imageInput);
        grayWarp.push(selectRed.getFront());
        final Gray8OffsetImage warpedRed = (Gray8OffsetImage) grayWarp.getFront();
        selectGreen.push(imageInput);
        grayWarp.push(selectGreen.getFront());
        final Gray8OffsetImage warpedGreen = (Gray8OffsetImage) grayWarp.getFront();
        selectBlue.push(imageInput);
        grayWarp.push(selectBlue.getFront());
        final Gray8OffsetImage warpedBlue = (Gray8OffsetImage) grayWarp.getFront();
        final RgbImage rgb = Gray3Bands2Rgb.push(warpedRed, warpedGreen, warpedBlue);
        super.setOutput(new RgbOffsetImage(rgb, warpedRed.getXOffset(), warpedRed.getYOffset()));
    }
    
    public void setWarp(final Integer[][] warp) throws ImageError {
        grayWarp.setWarp(warp);
    }
    
    public Vec2 warpVec(final Vec2 v) {
        return grayWarp.warpVec(v);
    }
}
