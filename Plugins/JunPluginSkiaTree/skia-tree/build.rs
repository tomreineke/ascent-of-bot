use std::collections::HashMap;
use std::env;
use std::path::PathBuf;

use anyhow::Result;
use cbindgen::{Config, ExportConfig};

fn main() -> Result<()> {
    let crate_dir = env::var("CARGO_MANIFEST_DIR")?;
    let package_name = env::var("CARGO_PKG_NAME")?;
    let output_file = target_dir()
        .join(format!("include/{}.h", package_name))
        .display()
        .to_string();
    let config = Config {
        namespace: Some("skiatree".to_string()),
        header: Some("#pragma once".to_string()),
        export: ExportConfig {
            exclude: vec![
                "Vec2f".to_string(),
                "Vec2i".to_string(),
                "InputState".to_string(),
            ],
            rename: HashMap::from([
                ("Vec2f".to_string(), "FVector2D".to_string()),
                ("Vec2i".to_string(), "FIntPoint".to_string()),
                ("Color4f".to_string(), "FLinearColor".to_string()),
                ("Point".to_string(), "FVector2D".to_string()),
                ("IPoint".to_string(), "FIntPoint".to_string()),
                ("IRect".to_string(), "FIntRect".to_string()),
                ("InputState".to_string(), "uint8_t".to_string()),

                ("Canvas".to_string(), "void".to_string()),
                ("Paint".to_string(), "void".to_string()),
            ]),
            ..Default::default()
        },
        ..Default::default()
    };
    cbindgen::generate_with_config(&crate_dir, config)?.write_to_file(&output_file);
    Ok(())
}

fn target_dir() -> PathBuf {
    if let Ok(target) = env::var("CARGO_TARGET_DIR") {
        PathBuf::from(target)
    } else {
        PathBuf::from(env::var("CARGO_MANIFEST_DIR").unwrap()).join("target")
    }
}
