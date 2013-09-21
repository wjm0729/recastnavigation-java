package org.recast.Detour.Include;

public class dtNavMeshCreateParams {
    /// @name Polygon Mesh Attributes
    /// Used to create the base navigation graph.
    /// See #rcPolyMesh for details related to these attributes.
    /// @{

    public short[] verts;			///< The polygon mesh vertices. [(x, y, z) * #vertCount] [Unit: vx]
    public int vertCount;							///< The number vertices in the polygon mesh. [Limit: >= 3]
    public short[] polys;			///< The polygon data. [Size: #polyCount * 2 * #nvp]
    public short[] polyFlags;		///< The user defined flags assigned to each polygon. [Size: #polyCount]
    public char[] polyAreas;			///< The user defined area ids assigned to each polygon. [Size: #polyCount]
    public int polyCount;							///< Number of polygons in the mesh. [Limit: >= 1]
    public int nvp;								///< Number maximum number of vertices per polygon. [Limit: >= 3]

    /// @}
    /// @name Height Detail Attributes (Optional)
    /// See #rcPolyMeshDetail for details related to these attributes.
    /// @{

    public int[] detailMeshes;		///< The height detail sub-mesh data. [Size: 4 * #polyCount]
    public float[] detailVerts;				///< The detail mesh vertices. [Size: 3 * #detailVertsCount] [Unit: wu]
    public int detailVertsCount;					///< The number of vertices in the detail mesh.
    public char[] detailTris;		///< The detail mesh triangles. [Size: 4 * #detailTriCount]
    public int detailTriCount;						///< The number of triangles in the detail mesh.

    /// @}
    /// @name Off-Mesh Connections Attributes (Optional)
    /// Used to define a custom point-to-point edge within the navigation graph, an
    /// off-mesh connection is a user defined traversable connection made up to two vertices,
    /// at least one of which resides within a navigation mesh polygon.
    /// @{

    /// Off-mesh connection vertices. [(ax, ay, az, bx, by, bz) * #offMeshConCount] [Unit: wu]
    public float[] offMeshConVerts;
    /// Off-mesh connection radii. [Size: #offMeshConCount] [Unit: wu]
    public float[] offMeshConRad;
    /// User defined flags assigned to the off-mesh connections. [Size: #offMeshConCount]
    public short[] offMeshConFlags;
    /// User defined area ids assigned to the off-mesh connections. [Size: #offMeshConCount]
    public char[] offMeshConAreas;
    /// The permitted travel direction of the off-mesh connections. [Size: #offMeshConCount]
    ///
    /// 0 = Travel only from endpoint A to endpoint B.<br/>
    /// #DT_OFFMESH_CON_BIDIR = Bidirectional travel.
    public char[] offMeshConDir;
    /// The user defined ids of the off-mesh connection. [Size: #offMeshConCount]
    public int[] offMeshConUserID;
    /// The number of off-mesh connections. [Limit: >= 0]
    public int offMeshConCount;

    /// @}
    /// @name Tile Attributes
    /// @note The tile grid/layer data can be left at zero if the destination is a single tile mesh.
    /// @{

    public int userId;	///< The user defined id of the tile.
    public int tileX;				///< The tile's x-grid location within the multi-tile destination mesh. (Along the x-axis.)
    public int tileY;				///< The tile's y-grid location within the multi-tile desitation mesh. (Along the z-axis.)
    public int tileLayer;			///< The tile's layer within the layered destination mesh. [Limit: >= 0] (Along the y-axis.)
    public float bmin[] = new float[3];			///< The minimum bounds of the tile. [(x, y, z)] [Unit: wu]
    public float bmax[] = new float[3];			///< The maximum bounds of the tile. [(x, y, z)] [Unit: wu]

    /// @}
    /// @name General Configuration Attributes
    /// @{

    public float walkableHeight;	///< The agent height. [Unit: wu]
    public float walkableRadius;	///< The agent radius. [Unit: wu]
    public float walkableClimb;	///< The agent maximum traversable ledge. (Up/Down) [Unit: wu]
    public float cs;				///< The xz-plane cell size of the polygon mesh. [Limit: > 0] [Unit: wu]
    public float ch;				///< The y-axis cell height of the polygon mesh. [Limit: > 0] [Unit: wu]

    /// True if a bounding volume tree should be built for the tile.
    /// @note The BVTree is not normally needed for layered navigation meshes.
    public boolean buildBvTree;
}
