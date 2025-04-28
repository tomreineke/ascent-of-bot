#pragma once

#define JUN_UNPACK(...) __VA_ARGS__
#define JUN_IF_SAME(X, Y) std::enable_if_t<std::is_same<JUN_UNPACK X, JUN_UNPACK Y>::value, int>
