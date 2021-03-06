package org.recast.DetourCrowd.Include;

public class dtObstacleAvoidanceParams implements Cloneable
{
	public float velBias;
	public float weightDesVel;
	public float weightCurVel;
	public float weightSide;
	public float weightToi;
	public float horizTime;
	public char gridSize;    ///< grid
	public char adaptiveDivs;    ///< adaptive
	public char adaptiveRings;    ///< adaptive
	public char adaptiveDepth;    ///< adaptive

	@Override
	public dtObstacleAvoidanceParams clone()
	{
		try
		{
			return (dtObstacleAvoidanceParams)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
