/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ide.common.vectordrawable;


import com.android.annotations.NonNull;

import java.awt.geom.Path2D;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Given an array of {@link VdPath.Node}, generates a Path2D object.
 * In other words, this is the engine which converts the pathData into
 * a Path2D object, which is able to draw on Swing components.
 * The logic and math here are the same as PathParser.java in framework.
 */
class VdNodeRender {
    private static final Logger LOGGER = Logger.getLogger(VdNodeRender.class.getSimpleName());

    public static void createPath(@NonNull VdPath.Node[] nodes, @NonNull Path2D path) {
        float[] current = new float[6];
        char lastCmd = ' ';
        for (VdPath.Node node : nodes) {
            addCommand(path, current, node.getType(), lastCmd, node.getParams());
            lastCmd = node.getType();
        }
    }

    private static void addCommand(@NonNull Path2D path, float[] current, char cmd, char lastCmd,
            float[] val) {
        int incr = 2;

        float cx = current[0];
        float cy = current[1];
        float cpx = current[2];
        float cpy = current[3];
        float loopX = current[4];
        float loopY = current[5];

        switch (cmd) {
            case 'z':
            case 'Z':
                path.closePath();
                cx = loopX;
                cy = loopY;
                //noinspection fallthrough
            case 'm':
            case 'M':
            case 'l':
            case 'L':
            case 't':
            case 'T':
                incr = 2;
                break;
            case 'h':
            case 'H':
            case 'v':
            case 'V':
                incr = 1;
                break;
            case 'c':
            case 'C':
                incr = 6;
                break;
            case 's':
            case 'S':
            case 'q':
            case 'Q':
                incr = 4;
                break;
            case 'a':
            case 'A':
                incr = 7;
        }

        for (int k = 0; k < val.length; k += incr) {
            boolean reflectCtrl;
            float tempReflectedX, tempReflectedY;

            switch (cmd) {
                case 'm':
                    cx += val[k];
                    cy += val[k + 1];
                    if (k > 0) {
                        // If a moveto is followed by multiple pairs of coordinates, the subsequent
                        // pairs are treated as implicit lineto commands.
                        path.lineTo(cx, cy);
                    } else {
                        path.moveTo(cx, cy);
                        loopX = cx;
                        loopY = cy;
                    }
                    break;

                case 'M':
                    cx = val[k];
                    cy = val[k + 1];
                    if (k > 0) {
                        // If a moveto is followed by multiple pairs of coordinates, the subsequent
                        // pairs are treated as implicit lineto commands.
                        path.lineTo(cx, cy);
                    } else {
                        path.moveTo(cx, cy);
                        loopX = cx;
                        loopY = cy;
                    }
                    break;

                case 'l':
                    cx += val[k];
                    cy += val[k + 1];
                    path.lineTo(cx, cy);
                    break;

                case 'L':
                    cx = val[k];
                    cy = val[k + 1];
                    path.lineTo(cx, cy);
                    break;

                case 'z':
                case 'Z':
                    path.closePath();
                    cx = loopX;
                    cy = loopY;
                    break;

                case 'h':
                    cx += val[k];
                    path.lineTo(cx, cy);
                    break;

                case 'H':
                    path.lineTo(val[k], cy);
                    cx = val[k];
                    break;

                case 'v':
                    cy += val[k];
                    path.lineTo(cx, cy);
                    break;

                case 'V':
                    path.lineTo(cx, val[k]);
                    cy = val[k];
                    break;

                case 'c':
                    path.curveTo(cx + val[k], cy + val[k + 1], cx + val[k + 2],
                            cy + val[k + 3], cx + val[k + 4], cy + val[k + 5]);
                    cpx = cx + val[k + 2];
                    cpy = cy + val[k + 3];
                    cx += val[k + 4];
                    cy += val[k + 5];
                    break;

                case 'C':
                    path.curveTo(val[k], val[k + 1], val[k + 2], val[k + 3], val[k + 4],
                            val[k + 5]);
                    cx = val[k + 4];
                    cy = val[k + 5];
                    cpx = val[k + 2];
                    cpy = val[k + 3];
                    break;

                case 's':
                    reflectCtrl = (lastCmd == 'c' || lastCmd == 's' || lastCmd == 'C' || lastCmd == 'S');
                    path.curveTo(reflectCtrl ? 2 * cx - cpx : cx, reflectCtrl ? 2 * cy - cpy : cy,
                            cx + val[k], cy + val[k + 1], cx + val[k + 2], cy + val[k + 3]);
                    cpx = cx + val[k];
                    cpy = cy + val[k + 1];
                    cx += val[k + 2];
                    cy += val[k + 3];
                    break;

                case 'S':
                    reflectCtrl = (lastCmd == 'c' || lastCmd == 's' || lastCmd == 'C' || lastCmd == 'S');
                    path.curveTo(reflectCtrl ? 2 * cx - cpx : cx, reflectCtrl ? 2 * cy - cpy : cy,
                            val[k], val[k + 1], val[k + 2], val[k + 3]);
                    cpx = (val[k]);
                    cpy = (val[k + 1]);
                    cx = val[k + 2];
                    cy = val[k + 3];
                    break;

                case 'q':
                    path.quadTo(cx + val[k], cy + val[k + 1], cx + val[k + 2],
                            cy + val[k + 3]);
                    cpx = cx + val[k];
                    cpy = cy + val[k + 1];
                    // Note that we have to update cpx first, since cx will be updated here.
                    cx += val[k + 2];
                    cy += val[k + 3];
                    break;

                case 'Q':
                    path.quadTo(val[k], val[k + 1], val[k + 2], val[k + 3]);
                    cx = val[k + 2];
                    cy = val[k + 3];
                    cpx = val[k];
                    cpy = val[k + 1];
                    break;

                case 't':
                    reflectCtrl = (lastCmd == 'q' || lastCmd == 't' || lastCmd == 'Q' || lastCmd == 'T');
                    tempReflectedX = reflectCtrl ? 2 * cx - cpx : cx;
                    tempReflectedY = reflectCtrl ? 2 * cy - cpy : cy;
                    path.quadTo(tempReflectedX, tempReflectedY, cx + val[k], cy + val[k + 1]);
                    cpx = tempReflectedX;
                    cpy = tempReflectedY;
                    cx += val[k];
                    cy += val[k + 1];
                    break;

                case 'T':
                    reflectCtrl = (lastCmd == 'q' || lastCmd == 't' || lastCmd == 'Q' || lastCmd == 'T');
                    tempReflectedX = reflectCtrl ? 2 * cx - cpx : cx;
                    tempReflectedY = reflectCtrl ? 2 * cy - cpy : cy;
                    path.quadTo(tempReflectedX, tempReflectedY, val[k], val[k + 1]);
                    cx = val[k];
                    cy = val[k + 1];
                    cpx = tempReflectedX;
                    cpy = tempReflectedY;
                    break;

                case 'a':
                    // (rx ry x-axis-rotation large-arc-flag sweep-flag x y)
                    drawArc(path, cx, cy, val[k + 5] + cx, val[k + 6] + cy,
                            Math.abs(val[k]), Math.abs(val[k + 1]), val[k + 2],
                            val[k + 3] != 0, val[k + 4] != 0);
                    cx += val[k + 5];
                    cy += val[k + 6];
                    cpx = cx;
                    cpy = cy;
                    break;

                case 'A':
                    drawArc(path, cx, cy, val[k + 5], val[k + 6],
                            Math.abs(val[k]), Math.abs(val[k + 1]), val[k + 2],
                            val[k + 3] != 0, val[k + 4] != 0);
                    cx = val[k + 5];
                    cy = val[k + 6];
                    cpx = cx;
                    cpy = cy;
                    break;
            }

            lastCmd = cmd;
        }
        current[0] = cx;
        current[1] = cy;
        current[2] = cpx;
        current[3] = cpy;
        current[4] = loopX;
        current[5] = loopY;
    }

    private static void drawArc(@NonNull Path2D p, double x0, double y0, double x1, double y1,
            double a, double b, double theta, boolean isMoreThanHalf, boolean isPositiveArc) {
        LOGGER.log(Level.FINE, "(" + x0 + "," + y0 + ")-(" + x1 + "," + y1
                + ") {" + a + " " + b + "}");
        // Convert rotation angle from degrees to radians.
        double thetaD = theta * Math.PI / 180.0f;
        // Pre-compute rotation matrix entries.
        double cosTheta = Math.cos(thetaD);
        double sinTheta = Math.sin(thetaD);
        // Transform (x0, y0) and (x1, y1) into unit space using (inverse) rotation,
        // followed by (inverse) scale.
        double x0p = (x0 * cosTheta + y0 * sinTheta) / a;
        double y0p = (-x0 * sinTheta + y0 * cosTheta) / b;
        double x1p = (x1 * cosTheta + y1 * sinTheta) / a;
        double y1p = (-x1 * sinTheta + y1 * cosTheta) / b;
        LOGGER.log(Level.FINE, "unit space (" + x0p + "," + y0p + ")-(" + x1p
                + "," + y1p + ")");
        // Compute differences and averages.
        double dx = x0p - x1p;
        double dy = y0p - y1p;
        double xm = (x0p + x1p) / 2;
        double ym = (y0p + y1p) / 2;
        // Solve for intersecting unit circles.
        double dsq = dx * dx + dy * dy;
        if (dsq == 0.0) {
            LOGGER.log(Level.FINE, " Points are coincident");
            return; // Points are coincident.
        }
        double disc = 1.0 / dsq - 1.0 / 4.0;
        if (disc < 0.0) {
            LOGGER.log(Level.FINE, "Points are too far apart " + dsq);
            double adjust = Math.sqrt(dsq) / 1.99999;
            drawArc(p, x0, y0, x1, y1, a * adjust, b * adjust, theta,
                    isMoreThanHalf, isPositiveArc);
            return; // Points are too far apart.
        }
        double s = Math.sqrt(disc);
        double sdx = s * dx;
        double sdy = s * dy;
        double cx;
        double cy;
        if (isMoreThanHalf == isPositiveArc) {
            cx = xm - sdy;
            cy = ym + sdx;
        } else {
            cx = xm + sdy;
            cy = ym - sdx;
        }

        double eta0 = Math.atan2(y0p - cy, x0p - cx);
        LOGGER.log(Level.FINE, "eta0 = Math.atan2(" + (y0p - cy) + ", " + (x0p - cx) + ") = "
                + Math.toDegrees(eta0));

        double eta1 = Math.atan2(y1p - cy, x1p - cx);
        LOGGER.log(Level.FINE, "eta1 = Math.atan2(" + (y1p - cy) + ", " + (x1p - cx) + ") = "
                + Math.toDegrees(eta1));
        double sweep = eta1 - eta0;
        if (isPositiveArc != (sweep >= 0)) {
            if (sweep > 0) {
                sweep -= 2 * Math.PI;
            } else {
                sweep += 2 * Math.PI;
            }
        }

        cx *= a;
        cy *= b;
        double tcx = cx;
        cx = cx * cosTheta - cy * sinTheta;
        cy = tcx * sinTheta + cy * cosTheta;
        LOGGER.log(Level.FINE, "cx = " + cx + ", cy = " + cy + ", a = " + a + ", b = " + b
                + ", x0 = " + x0 + ", y0 = " + y0 + ", thetaD = " + Math.toDegrees(thetaD)
                + ", eta0 = " + Math.toDegrees(eta0) + ", sweep = " + Math.toDegrees(sweep));

        arcToBezier(p, cx, cy, a, b, x0, y0, thetaD, eta0, sweep);
    }

    /**
     * Converts an arc to cubic Bezier segments and records them in p.
     *
     * @param p The target for the cubic Bezier segments
     * @param cx The x coordinate center of the ellipse
     * @param cy The y coordinate center of the ellipse
     * @param a The radius of the ellipse in the horizontal direction
     * @param b The radius of the ellipse in the vertical direction
     * @param e1x E(eta1) x coordinate of the starting point of the arc
     * @param e1y E(eta2) y coordinate of the starting point of the arc
     * @param theta The angle that the ellipse bounding rectangle makes with the horizontal plane
     * @param start The start angle of the arc on the ellipse
     * @param sweep The angle (positive or negative) of the sweep of the arc on the ellipse
     */
    private static void arcToBezier(@NonNull Path2D p, double cx, double cy, double a, double b,
            double e1x, double e1y, double theta, double start, double sweep) {
        // Taken from equations at:
        // http://spaceroots.org/documents/ellipse/node8.html
        // and http://www.spaceroots.org/documents/ellipse/node22.html
        // Maximum of 45 degrees per cubic Bezier segment
        int numSegments = (int) Math.ceil(Math.abs(sweep * 4 / Math.PI));

        double eta1 = start;
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double cosEta1 = Math.cos(eta1);
        double sinEta1 = Math.sin(eta1);
        double ep1x = (-a * cosTheta * sinEta1) - (b * sinTheta * cosEta1);
        double ep1y = (-a * sinTheta * sinEta1) + (b * cosTheta * cosEta1);

        double anglePerSegment = sweep / numSegments;
        for (int i = 0; i < numSegments; i++) {
            double eta2 = eta1 + anglePerSegment;
            double sinEta2 = Math.sin(eta2);
            double cosEta2 = Math.cos(eta2);
            double e2x = cx + (a * cosTheta * cosEta2) - (b * sinTheta * sinEta2);
            double e2y = cy + (a * sinTheta * cosEta2) + (b * cosTheta * sinEta2);
            double ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2;
            double ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2;
            double tanDiff2 = Math.tan((eta2 - eta1) / 2);
            double alpha = Math.sin(eta2 - eta1)
                    * (Math.sqrt(4 + (3 * tanDiff2 * tanDiff2)) - 1) / 3;
            double q1x = e1x + alpha * ep1x;
            double q1y = e1y + alpha * ep1y;
            double q2x = e2x - alpha * ep2x;
            double q2y = e2y - alpha * ep2y;

            p.curveTo(q1x, q1y, q2x, q2y, e2x, e2y);
            eta1 = eta2;
            e1x = e2x;
            e1y = e2y;
            ep1x = ep2x;
            ep1y = ep2y;
        }
    }
}
