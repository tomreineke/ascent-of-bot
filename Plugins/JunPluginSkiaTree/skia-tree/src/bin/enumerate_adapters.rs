use std::ffi::c_void;
use std::mem::size_of;
use std::ptr::null_mut;

use anyhow::{format_err, Result};
use winapi::Interface;
use winapi::shared::dxgi::{CreateDXGIFactory1, DXGI_ADAPTER_DESC1, DXGI_ADAPTER_FLAG_SOFTWARE, IDXGIAdapter1, IDXGIFactory1};
use winapi::shared::dxgiformat::DXGI_FORMAT_B8G8R8A8_UNORM;
use winapi::shared::winerror::{DXGI_ERROR_NOT_FOUND, S_OK};
use winapi::um::d3d12::{D3D12_FEATURE_DATA_FORMAT_SUPPORT, D3D12_FEATURE_FORMAT_SUPPORT, D3D12_FORMAT_SUPPORT1_NONE, D3D12_FORMAT_SUPPORT1_RENDER_TARGET, D3D12_FORMAT_SUPPORT1_TEXTURE2D, D3D12_FORMAT_SUPPORT2_NONE, D3D12CreateDevice, ID3D12Device};
use winapi::um::d3dcommon::D3D_FEATURE_LEVEL_12_0;
use winapi::um::unknwnbase::IUnknown;
use winapi::um::winnt::LUID;
use wio::com::ComPtr;

fn create_dxgi_factory() -> Result<ComPtr<IDXGIFactory1>> {
    let mut pointer: *mut IDXGIFactory1 = null_mut();
    unsafe {
        CreateDXGIFactory1(&IDXGIFactory1::uuidof(), &mut pointer as *mut *mut IDXGIFactory1 as *mut *mut c_void)
    };
    if pointer.is_null() {
        Err(format_err!("CreateDXGIFactory1 failed"))
    } else {
        Ok(unsafe { ComPtr::from_raw(pointer) })
    }
}

struct AdapterIterator {
    dxgi_factory: ComPtr<IDXGIFactory1>,
    next_index: u32
}

impl AdapterIterator {
    fn new(dxgi_factory: &ComPtr<IDXGIFactory1>) -> AdapterIterator {
        AdapterIterator {
            dxgi_factory: dxgi_factory.clone(),
            next_index: 0
        }
    }
}

impl Iterator for AdapterIterator {
    type Item = ComPtr<IDXGIAdapter1>;

    fn next(&mut self) -> Option<Self::Item> {
        let index = self.next_index;
        let mut adapter_pointer: *mut IDXGIAdapter1 = null_mut();
        let result = unsafe {
            self.dxgi_factory.EnumAdapters1(index, &mut adapter_pointer)
        };
        if result == DXGI_ERROR_NOT_FOUND {
            None
        } else {
            self.next_index += 1;
            Some(unsafe { ComPtr::from_raw(adapter_pointer) })
        }
    }
}

fn select_device(dxgi_factory: &ComPtr<IDXGIFactory1>) -> Result<()> {
    for (index, adapter) in AdapterIterator::new(&dxgi_factory).enumerate() {
        let mut desc = DXGI_ADAPTER_DESC1 {
            Description: [0; 128],
            VendorId: 0,
            DeviceId: 0,
            SubSysId: 0,
            Revision: 0,
            DedicatedVideoMemory: 0,
            DedicatedSystemMemory: 0,
            SharedSystemMemory: 0,
            AdapterLuid: LUID { LowPart: 0, HighPart: 0 },
            Flags: 0,
        };
        unsafe { adapter.GetDesc1(&mut desc) };
        let name = String::from_utf16_lossy(&desc.Description);
        println!();
        println!("{}. Adapter: {}", index, name);
        if desc.Flags & DXGI_ADAPTER_FLAG_SOFTWARE != 0 {
            println!("REJECT due to DXGI_ADAPTER_FLAG_SOFTWARE set");
            continue;
        }
        println!("- is real hardware");

        let mut device_pointer: *mut ID3D12Device = null_mut();
        unsafe {
            D3D12CreateDevice(
                adapter.as_raw() as *mut IUnknown,
                D3D_FEATURE_LEVEL_12_0,
                &ID3D12Device::uuidof(),
                &mut device_pointer as *mut *mut ID3D12Device as *mut *mut c_void
            )
        };
        if device_pointer.is_null() {
            println!("REJECT as device could not be created");
            continue;
        }
        println!("- successfully created device");
        let device = unsafe { ComPtr::from_raw(device_pointer) };
        let mut feature_data_format_suuport = D3D12_FEATURE_DATA_FORMAT_SUPPORT {
            Format: DXGI_FORMAT_B8G8R8A8_UNORM,
            Support1: D3D12_FORMAT_SUPPORT1_NONE,
            Support2: D3D12_FORMAT_SUPPORT2_NONE
        };
        if unsafe {
            device.CheckFeatureSupport(
                D3D12_FEATURE_FORMAT_SUPPORT,
                &mut feature_data_format_suuport as *mut D3D12_FEATURE_DATA_FORMAT_SUPPORT as *mut c_void,
                size_of::<D3D12_FEATURE_DATA_FORMAT_SUPPORT>() as u32
            )
        } != S_OK {
            println!("REJECT due to missing support for DXGI_FORMAT_B8G8R8A8_UNORM");
            continue;
        }
        println!("- support for DXGI_FORMAT_B8G8R8A8_UNORM");
        let caps = D3D12_FORMAT_SUPPORT1_TEXTURE2D | D3D12_FORMAT_SUPPORT1_RENDER_TARGET;
        if feature_data_format_suuport.Support1 & caps != caps {
            println!("REJECT as DXGI_FORMAT_B8G8R8A8_UNORM does not support TEXTURE2D and RENDER_TARGET");
            continue;
        }
        println!("- capability for TEXTURE2D and RENDER_TARGET");
        println!("ACCEPTED");
    }
    println!();
    println!("Finished enumeration");
    Ok(())
}

pub fn main() -> Result<()> {
    println!("Hi.");
    // enable_debug_interface()?;
    let dxgi_factory = create_dxgi_factory()?;
    select_device(&dxgi_factory)?;
    Ok(())
}
