#![feature(result_option_inspect)]
#![feature(vec_into_raw_parts)]
#![feature(local_key_cell_methods)]
#![feature(int_roundings)]

#![allow(dead_code)]

extern crate core;

mod asserts;
mod background;
mod geo;
mod direct_context;
mod drawing;
mod enumset_store;
mod error;
mod ffi;
mod forest;
mod immediate;
mod input;
mod library;
mod layout;
mod log;
mod node;
mod rc_util;
mod slot_maps;
mod style;
mod subscriber;
mod table;
