use std::cell::RefCell;
use std::time::Duration;

use backtrace::Backtrace;
use skia_safe::gpu::{DirectContext, RecordingContext};
use slotmap::SlotMap;
use tracing::instrument;
use tracing::subscriber::DefaultGuard;

use crate::direct_context;
use crate::error::guarded_box;
use crate::input::InputUpCalls;
use crate::log::log;
use crate::node::{NodeElement, NodeKey};
use crate::subscriber::LogSubscriber;

pub struct SkiaTreeLibrary {
    pub direct_context: DirectContext,
    pub recording_context: RecordingContext,
    pub tracing_guard: DefaultGuard,
    pub adapter_name: String,
    pub nodes: SlotMap<NodeKey, RefCell<NodeElement>>,
    pub input_upcalls: InputUpCalls,
    pub primary_button_index: i32,
    pub tooltip_delay: Duration
}

#[no_mangle]
pub extern "C" fn skiatree_library_new(debug: bool, input_upcalls: InputUpCalls, primary_button_index: i32) -> *mut SkiaTreeLibrary {
    std::panic::set_hook(Box::new(|panic_info| {
        let backtrace = Backtrace::new();
        let location_string = if let Some(location) = panic_info.location() {
            format!("file {} in line {}\n{backtrace:?}", location.file(), location.line())
        } else {
            "unknown location".to_string()
        };
        if let Some(s) = panic_info.payload().downcast_ref::<&str>() {
            log!("panic occurred at {location_string}: {s:?}\n{backtrace:?}");
        } else {
            log!("panic occurred at {location_string}\n{backtrace:?}");
        }
    }));
    let tracing_guard = LogSubscriber::install();

    guarded_box(|| {
        let (direct_context, adapter_name) = direct_context::create_direct_context(debug)?;
        let recording_context = RecordingContext::from(direct_context.clone());
        Ok(Box::new(SkiaTreeLibrary {
            direct_context,
            recording_context,
            tracing_guard,
            adapter_name,
            nodes: SlotMap::default(),
            input_upcalls,
            primary_button_index,
            tooltip_delay: Duration::from_millis(500)
        }))
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_library_delete(skiatree: *mut SkiaTreeLibrary) {
    drop(unsafe { Box::from_raw(skiatree) })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_library_flush_and_submit(skiatree: *mut SkiaTreeLibrary) {
    let skiatree = unsafe { &mut *skiatree };
    skiatree.direct_context.flush_and_submit();
}
