package org.recast.Recast.Source;

import org.recast.Recast.Include.rcContext;
import org.recast.Recast.Include.rcHeightfield;
import org.recast.Recast.Include.rcSpan;
import org.recast.Recast.Include.rcTimerLabel;

public class RecastFilter extends RecastImpl
{
	public void rcFilterLowHangingWalkableObstacles(rcContext ctx, int walkableClimb, rcHeightfield solid)
	{
//    }
		//
// Copyright (c) 2009-2010 Mikko Mononen memon@inside.org
//
// This software is provided 'as-is', without any express or implied
// warranty.  In no event will the authors be held liable for any damages
// arising from the use of this software.
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it
// freely, subject to the following restrictions:
// 1. The origin of this software must not be misrepresented; you must not
//    claim that you wrote the original software. If you use this software
//    in a product, an acknowledgment in the product documentation would be
//    appreciated but is not required.
// 2. Altered source versions must be plainly marked as such, and must not be
//    misrepresented as being the original software.
// 3. This notice may not be removed or altered from any source distribution.
//

//        #define _USE_MATH_DEFINES
//        #include <math.h>
//        #include <stdio.h>
//        #include "Recast.h"
//        #include "RecastAssert.h"

/// @par
///
/// Allows the formation of walkable regions that will flow over low lying
/// objects such as curbs, and up structures such as stairways.
///
/// Two neighboring spans are walkable if: <tt>rcAbs(currentSpan.smax - neighborSpan.smax) < waklableClimb</tt>
///
/// @warning Will override the effect of #rcFilterLedgeSpans.  So if both filters are used, call
/// #rcFilterLedgeSpans after calling this filter.
///
/// @see rcHeightfield, rcConfig
//        void rcFilterLowHangingWalkableObstacles(rcContext* ctx, const int walkableClimb, rcHeightfield& solid)
//        {
//            rcAssert(ctx);

		ctx.startTimer(rcTimerLabel.RC_TIMER_FILTER_LOW_OBSTACLES);

		int w = solid.width;
		int h = solid.height;

		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				rcSpan ps = null;
				boolean previousWalkable = false;
				char previousArea = RC_NULL_AREA;

				for (rcSpan s = solid.spans[x + y * w]; s != null; ps = s, s = s.next)
				{
					boolean walkable = s.getArea() != RC_NULL_AREA;
					// If current span is not walkable, but there is walkable
					// span just below it, mark the span above it walkable too.
					if (!walkable && previousWalkable)
					{
						if (rcAbs((int)s.getSmax() - (int)ps.getSmax()) <= walkableClimb)
							s.setArea(previousArea);
					}
					// Copy walkable flag so that it cannot propagate
					// past multiple non-walkable objects.
					previousWalkable = walkable;
					previousArea = (char)s.getArea();
				}
			}
		}

		ctx.stopTimer(rcTimerLabel.RC_TIMER_FILTER_LOW_OBSTACLES);
	}

	/// @par
///
/// A ledge is a span with one or more neighbors whose maximum is further away than @p walkableClimb
/// from the current span's maximum.
/// This method removes the impact of the overestimation of conservative voxelization
/// so the resulting mesh will not have regions hanging in the air over ledges.
///
/// A span is a ledge if: <tt>rcAbs(currentSpan.smax - neighborSpan.smax) > walkableClimb</tt>
///
/// @see rcHeightfield, rcConfig
	public void rcFilterLedgeSpans(rcContext ctx, int walkableHeight, int walkableClimb, rcHeightfield solid)
	{
//            rcAssert(ctx);

		ctx.startTimer(rcTimerLabel.RC_TIMER_FILTER_BORDER);

		int w = solid.width;
		int h = solid.height;
		int MAX_HEIGHT = 0xffff;

		// Mark border spans.
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				for (rcSpan s = solid.spans[x + y * w]; s != null; s = s.next)
				{
					// Skip non walkable spans.
					if (s.getArea() == RC_NULL_AREA)
						continue;

					int bot = (int)(s.getSmax());
					int top = s.next != null ? (int)(s.next.getSmin()) : MAX_HEIGHT;

					// Find neighbours minimum height.
					int minh = MAX_HEIGHT;

					// Min and max height of accessible neighbours.
					int asmin = s.getSmax();
					int asmax = s.getSmax();

					for (int dir = 0; dir < 4; ++dir)
					{
						int dx = x + rcGetDirOffsetX(dir);
						int dy = y + rcGetDirOffsetY(dir);
						// Skip neighbours which are out of bounds.
						if (dx < 0 || dy < 0 || dx >= w || dy >= h)
						{
							minh = rcMin(minh, -walkableClimb - bot);
							continue;
						}

						// From minus infinity to the first span.
						rcSpan ns = solid.spans[dx + dy * w];
						int nbot = -walkableClimb;
						int ntop = ns != null ? (int)ns.getSmin() : MAX_HEIGHT;
						// Skip neightbour if the gap between the spans is too small.
						if (rcMin(top, ntop) - rcMax(bot, nbot) > walkableHeight)
							minh = rcMin(minh, nbot - bot);

						// Rest of the spans.
						for (ns = solid.spans[dx + dy * w]; ns != null; ns = ns.next)
						{
							nbot = (int)ns.getSmax();
							ntop = ns.next != null ? (int)ns.next.getSmin() : MAX_HEIGHT;
							// Skip neightbour if the gap between the spans is too small.
							if (rcMin(top, ntop) - rcMax(bot, nbot) > walkableHeight)
							{
								minh = rcMin(minh, nbot - bot);

								// Find min/max accessible neighbour height.
								if (rcAbs(nbot - bot) <= walkableClimb)
								{
									if (nbot < asmin) asmin = nbot;
									if (nbot > asmax) asmax = nbot;
								}

							}
						}
					}

					// The current span is close to a ledge if the drop to any
					// neighbour span is less than the walkableClimb.
					if (minh < -walkableClimb)
						s.setArea(RC_NULL_AREA);

					// If the difference between all neighbours is too large,
					// we are at steep slope, mark the span as ledge.
					if ((asmax - asmin) > walkableClimb)
					{
						s.setArea(RC_NULL_AREA);
					}
				}
			}
		}

		ctx.stopTimer(rcTimerLabel.RC_TIMER_FILTER_BORDER);
	}

	/// @par
///
/// For this filter, the clearance above the span is the distance from the span's
/// maximum to the next higher span's minimum. (Same grid column.)
///
/// @see rcHeightfield, rcConfig
	public void rcFilterWalkableLowHeightSpans(rcContext ctx, int walkableHeight, rcHeightfield solid)
	{
//            rcAssert(ctx);

		ctx.startTimer(rcTimerLabel.RC_TIMER_FILTER_WALKABLE);

		int w = solid.width;
		int h = solid.height;
		int MAX_HEIGHT = 0xffff;

		// Remove walkable flag from spans which do not have enough
		// space above them for the agent to stand there.
		for (int y = 0; y < h; ++y)
		{
			for (int x = 0; x < w; ++x)
			{
				for (rcSpan s = solid.spans[x + y * w]; s != null; s = s.next)
				{
					int bot = (int)(s.getSmax());
					int top = s.next != null ? (int)(s.next.getSmin()) : MAX_HEIGHT;
					if ((top - bot) <= walkableHeight)
						s.setArea(RC_NULL_AREA);
				}
			}
		}

		ctx.stopTimer(rcTimerLabel.RC_TIMER_FILTER_WALKABLE);
	}


}
