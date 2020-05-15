#ifndef __DEBUG_H__
#define __DEBUG_H__

#include "android/log.h"
static const char *TAG="JniOutPrint";

#define DEBUG
#if defined(DEBUG)
#define debug(fmt,args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args);
#else
#define debug(fmt,args...)
#endif

#endif // __DEBUG_H__
