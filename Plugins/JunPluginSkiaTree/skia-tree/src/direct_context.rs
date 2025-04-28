use anyhow::Result;
use skia_safe::gpu::DirectContext;

mod d3d;

pub fn create_direct_context(debug: bool) -> Result<(DirectContext, String)> {
    d3d::create_direct_context(debug)
}
