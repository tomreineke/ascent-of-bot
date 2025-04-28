use std::cell::{RefCell, Ref, RefMut};

use anyhow::{anyhow, Result};
use slotmap::{Key, KeyData, SlotMap};

pub trait SlotMapExt<K: Key, V> {
    fn acc(&self, key: K) -> Result<Ref<V>>;

    fn acc_mut(&self, key: K) -> Result<RefMut<V>>;

    fn acc_remove(&mut self, key: K) -> Result<V>;
}

impl<K: Key, V> SlotMapExt<K, V> for SlotMap<K, RefCell<V>> {
    fn acc(&self, key: K) -> Result<Ref<V>> {
        self.get(key).map(|cell| cell.borrow())
            .ok_or_else(|| anyhow!("Cannot find slotmap entry with key {:?} for acc", key))
    }

    fn acc_mut(&self, key: K) -> Result<RefMut<V>> {
        self.get(key).map(|cell| cell.borrow_mut())
            .ok_or_else(|| anyhow!("Cannot find slotmap entry with key {:?} for acc_mut", key))
    }

    fn acc_remove(&mut self, key: K) -> Result<V> {
        self.remove(key).map(|cell| cell.into_inner())
            .ok_or_else(|| anyhow!("Cannot find slotmap entry with key {:?} for remove", key))
    }
}

pub trait KeyExt {
    fn from_ffi(key: u64) -> Self;
}

impl<K: Key> KeyExt for K {
    fn from_ffi(key: u64) -> Self {
        K::from(KeyData::from_ffi(key))
    }
}
