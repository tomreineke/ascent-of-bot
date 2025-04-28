use std::error::Error;
use std::fmt::{Debug, Write};
use std::sync::atomic::{AtomicU64, Ordering};

use tracing::{Event, Id, Metadata, Subscriber};
use tracing::field::{Field, ValueSet, Visit};
use tracing::span::{Attributes, Record};
use tracing::subscriber::DefaultGuard;

use crate::log::log;

pub struct LogSubscriber {
    next_span_id: AtomicU64
}

impl LogSubscriber {
    pub fn install() -> DefaultGuard {
        tracing::subscriber::set_default(LogSubscriber {
            next_span_id: AtomicU64::new(1)
        })
    }
}

impl Subscriber for LogSubscriber {
    fn enabled(&self, _metadata: &Metadata<'_>) -> bool {
        true
    }

    fn new_span(&self, span: &Attributes<'_>) -> Id {
        let id = self.next_span_id.fetch_add(1, Ordering::Relaxed);
        let metadata = span.metadata();
        log!("TRACE [{id}] call {}({})", metadata.name(), ArgVisitor::process(span.values()));
        Id::from_u64(id)
    }

    fn record(&self, span: &Id, values: &Record<'_>) {
        log!("TRACE [{}] record {values:?}", span.into_u64());
    }

    fn record_follows_from(&self, span: &Id, follows: &Id) {
        log!("TRACE [{}] record_follows_from {}", span.into_u64(), follows.into_u64());
    }

    fn event(&self, event: &Event<'_>) {
        log!("TRACE {event:?}");
    }

    fn enter(&self, _span: &Id) {}

    fn exit(&self, span: &Id) {
        log!("TRACE [{}] return", span.into_u64());
    }
}

#[derive(Default)]
struct ArgVisitor {
    string: String,
    arg_index: usize,
    arg_count: usize
}

impl ArgVisitor {
    fn process(value_set: &ValueSet) -> String {
        let mut visitor = ArgVisitor {
            string: String::new(),
            arg_index: 0,
            arg_count: value_set.len()
        };
        value_set.record(&mut visitor);
        visitor.string
    }

    fn separator(&mut self) {
        if self.arg_index + 1 < self.arg_count {
            write!(self.string, ", ").unwrap();
        }
        self.arg_index += 1;
    }
}

impl Visit for ArgVisitor {
    fn record_f64(&mut self, field: &Field, value: f64) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_i64(&mut self, field: &Field, value: i64) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_u64(&mut self, field: &Field, value: u64) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_i128(&mut self, field: &Field, value: i128) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_u128(&mut self, field: &Field, value: u128) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_bool(&mut self, field: &Field, value: bool) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_str(&mut self, field: &Field, value: &str) {
        write!(self.string, r#"{field}="{value}""#).unwrap();
        self.separator();
    }

    fn record_error(&mut self, field: &Field, value: &(dyn Error + 'static)) {
        write!(self.string, "{field}={value}").unwrap();
        self.separator();
    }

    fn record_debug(&mut self, field: &Field, value: &dyn Debug) {
        write!(self.string, "{field}={value:?}").unwrap();
        self.separator();
    }
}

