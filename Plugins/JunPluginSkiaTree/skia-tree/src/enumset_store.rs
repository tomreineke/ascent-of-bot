use std::marker::PhantomData;
use std::ops::{Index, IndexMut, Shl};

use enumset::{EnumSet, EnumSetTypeWithRepr};
use smallvec::SmallVec;

pub struct EnumSetMap<T: EnumSetTypeWithRepr<Repr = u8>, V: Clone + Default> {
    values: SmallVec<[Option<V>; 8]>,
    // values: Vec<Option<V>>,
    default_value: V,
    phantom: PhantomData<T>
}

impl<T: EnumSetTypeWithRepr<Repr = u8>, V: Clone + Default> EnumSetMap<T, V> {
    pub fn remove(&mut self, index: EnumSet<T>) {
        self.values[index.as_repr() as usize].take();
    }

    pub fn put(&mut self, index: EnumSet<T>, value: V) {
        self.values[index.as_repr() as usize] = Some(value);
    }
}

impl<T: EnumSetTypeWithRepr<Repr = u8>, V: Clone + Default> Default for EnumSetMap<T, V> {
    fn default() -> EnumSetMap<T, V> {
        EnumSetMap {
            values: SmallVec::from_elem(None, 1usize.shl(EnumSet::<T>::bit_width() as usize)),
            default_value: V::default(),
            phantom: PhantomData
        }
    }
}

impl<T: EnumSetTypeWithRepr<Repr = u8>, V: Clone + Default> Index<EnumSet<T>> for EnumSetMap<T, V> {
    type Output = V;
    
    fn index(&self, mut index: EnumSet<T>) -> &Self::Output {
        let value = self.values[index.as_repr() as usize].as_ref();
        if value.is_some() {
            return value.unwrap();
        }
        for flag in <EnumSet<T>>::all() {
            if index.remove(flag) {
                let value = &self.values[index.as_repr() as usize].as_ref();
                if value.is_some() {
                    return value.unwrap();
                }
            }
        }
        &self.default_value
    }
}

impl<T: EnumSetTypeWithRepr<Repr = u8>, V: Clone + Default> IndexMut<EnumSet<T>> for EnumSetMap<T, V> {
    fn index_mut(&mut self, mut index: EnumSet<T>) -> &mut Self::Output {
        if self.values[index.as_repr() as usize].is_some() {
            return self.values[index.as_repr() as usize].as_mut().unwrap();
        }
        for flag in <EnumSet<T>>::all() {
            if index.remove(flag) {
                if self.values[index.as_repr() as usize].is_some() {
                    return self.values[index.as_repr() as usize].as_mut().unwrap();
                }
            }
        }
        &mut self.default_value
    }
}
